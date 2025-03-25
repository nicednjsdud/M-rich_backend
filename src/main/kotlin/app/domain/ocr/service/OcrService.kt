package app.domain.ocr.service

import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.protobuf.ByteString
import java.io.File

class OcrService {

    fun extractTableData(imageFile: File): List<Map<String, String>> {
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

    fun parseTableData(ocrText: String): List<Map<String, String>> {
        val tableData = mutableListOf<Map<String, String>>()
        val lines = ocrText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        val firstDateIndex = lines.indexOfFirst { dateRegex.containsMatchIn(it) }
        if (firstDateIndex == -1) return emptyList() // 날짜 형식이 없으면 종료

        val dataLines = lines.drop(firstDateIndex)

        var currentDate = ""
        var tempItem = mutableListOf<String>()

        for (line in dataLines) {
            val dateMatch = dateRegex.find(line)
            if (dateMatch != null) {
                currentDate = dateMatch.value
                continue
            }

            tempItem.add(line)

            if (tempItem.size >= 4) {
                val (itemName, priceText, status, process) = tempItem.take(4)
                tableData.add(
                    mapOf(
                        "거래날짜" to currentDate,
                        "아이템이름" to itemName,
                        "금액" to priceText.replace("[^\\d,]", ""),
                        "상태" to status,
                        "처리" to process
                    )
                )
                tempItem.clear()
            }
        }

        return tableData
    }

}
