package app.domain.ocr.service

import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.TesseractException
import java.io.File

class OcrService {
    private val tesseract: Tesseract = Tesseract()

    init {
        // 환경 변수 설정
        System.setProperty("jna.library.path", "/opt/homebrew/lib")
        System.setProperty("TESSDATA_PREFIX", "/opt/homebrew/share/tessdata/")
        System.setProperty("java.library.path", "/opt/homebrew/lib")

        // Tesseract 설정
        tesseract.setDatapath("/opt/homebrew/share/tessdata/") // 데이터 경로 설정 (Mac ARM)
        tesseract.setLanguage("kor+eng") // 한국어 + 영어 설정
    }

    fun extractTableData(imageFile: File): List<Map<String, String>> {
        return try {
            // 이미지 전처리 수행 후 저장된 이미지 경로
            val preprocessedFile = File("uploads/preprocessed.png")

            // 이미지 전처리 수행
            ImagePreprocessor.preprocessImage(imageFile.absolutePath, preprocessedFile.absolutePath)

            // OCR 수행 (전처리된 이미지 사용)
            val extractedText = tesseract.doOCR(preprocessedFile)

            parseTableData(extractedText)
        } catch (e: TesseractException) {
            println("OCR 처리 중 오류 발생: ${e.message}")
            emptyList()
        }
    }

    private fun parseTableData(ocrText: String): List<Map<String, String>> {
        println("OCR 추출 텍스트: $ocrText")
        val tableData = mutableListOf<Map<String, String>>()
        val lines = ocrText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        // 거래 날짜 정규식 (YYYY-MM-DD)
        val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        var currentDate = ""

        for (line in lines) {
            val dateMatch = dateRegex.find(line)
            if (dateMatch != null) {
                currentDate = dateMatch.value
                continue
            }

            // 데이터 추출
            val itemRegex = Regex("""(.+?)\s+(판매 완료|판매 중|대기 중)\s+([\d,]+)\s+(대금수령|처리 없음)""")
            val matchEtc = itemRegex.find(line)

            if (matchEtc != null) {
                val (item, status, price, action) = matchEtc.destructured
                if (currentDate.isNotEmpty()) {
                    tableData.add(
                        mapOf(
                            "거래날짜" to currentDate,
                            "아이템이름" to item.trim(),
                            "상태" to status.trim(),
                            "금액" to price.trim(),
                            "처리" to action.trim()
                        )
                    )
                }
            }
        }
        return tableData
    }
}
