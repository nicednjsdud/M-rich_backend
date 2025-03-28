package app.api

import app.domain.ocr.service.OcrService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.ocrRoutes(ocrService: OcrService) {
    get("/upload") {
        println("upload")
        call.respondFile(File("src/main/resources/static/testOcr.html"))
    }
    post("/ocr") {
        val multipart = call.receiveMultipart()
        var uploadedFile: File? = null

        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                val file = File("uploads/${part.originalFileName}")
                file.parentFile.mkdirs()
                file.writeBytes(part.streamProvider().readBytes())
                uploadedFile = file
            }
            part.dispose()
        }

        if (uploadedFile != null) {
            val extractedText = ocrService.extractTableData(uploadedFile!!)
            call.respond(HttpStatusCode.OK, extractedText)
        } else {
            call.respond(HttpStatusCode.BadRequest, "파일이 업로드되지 않았습니다.")
        }
    }
}