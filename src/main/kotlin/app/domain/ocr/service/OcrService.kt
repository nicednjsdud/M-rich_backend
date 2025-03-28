package app.domain.ocr.service

import app.domain.ocr.dto.OcrResponse
import app.utils.TradeStatus
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.protobuf.ByteString
import java.io.File

class OcrService {

    fun extractTableData(imageFile: File): List<OcrResponse> {
        return try {
            val ocrText = extractTextFromImage(imageFile.absolutePath)
            println("OCR 결과: $ocrText")
            return parseTableData(ocrText)
        } catch (e: Exception) {
            println("OCR 처리 중 오류 발생: ${e.message}")
            emptyList()
        } finally {
            imageFile.delete()
        }
    }

    private fun extractTextFromImage(imagePath: String): String {
        val imgBytes = ByteString.readFrom(File(imagePath).inputStream())
        val image = Image.newBuilder().setContent(imgBytes).build()
        val feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build()
        val request = AnnotateImageRequest.newBuilder()
            .addFeatures(feature)
            .setImage(image)
            .build()

        ImageAnnotatorClient.create().use { vision ->
            val response = vision.batchAnnotateImages(listOf(request))
            val annotation = response.responsesList[0].fullTextAnnotation
            return annotation?.text ?: "텍스트 없음"
        }
    }

    fun parseTableData(ocrText: String): List<OcrResponse> {
        val result = mutableListOf<OcrResponse>()
        val lines = ocrText.lines().map { it.trim() }.filter { it.isNotEmpty() }

        val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        val priceRegex = Regex("""[\d,]{3,}""")
        val korPriceRegex = Regex("""\(\d+억\s?\d+만\s?\d+\)""")

        var currentDate = ""
        var i = 0

        while (i < lines.size) {
            val line = lines[i]

            // 날짜로 시작하는 블록 시작
            if (dateRegex.matches(line)) {
                currentDate = line
                val itemName = lines.getOrNull(i + 1) ?: ""
                val statusLine = lines.getOrNull(i + 2) ?: ""
                val priceLine = lines.getOrNull(i + 3) ?: ""
                val korPriceLine = lines.getOrNull(i + 4)?.takeIf { korPriceRegex.containsMatchIn(it) } ?: ""
                val finalPriceLine = if (korPriceLine.isNotEmpty()) lines.getOrNull(i + 5) else lines.getOrNull(i + 4)

                // 가격 확인
                val price = priceLine.replace("[^\\d]", "").toIntOrNull()
                    ?: finalPriceLine?.replace("[^\\d]", "")?.toIntOrNull() ?: 0

                // 상태 확인
                val status = when {
                    statusLine.contains("판매") -> TradeStatus.COMPLETE
                    else -> TradeStatus.CANCEL
                }

                result.add(
                    OcrResponse(
                        tradeDate = currentDate,
                        itemName = itemName,
                        price = price,
                        korPrice = korPriceLine,
                        status = status
                    )
                )

                // 다음 블록으로 이동
                i += if (korPriceLine.isNotEmpty()) 6 else 5
            } else {
                i++
            }
        }
        return result
    }
}
