import org.bytedeco.javacpp.Loader
import org.bytedeco.javacpp.indexer.FloatIndexer
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_core.*
import org.bytedeco.opencv.global.opencv_imgcodecs.*
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Size

object ImagePreprocessor {
    fun preprocessImage(inputPath: String, outputPath: String) {
        Loader.load(opencv_core::class.java)

        val image = imread(inputPath, IMREAD_GRAYSCALE)

        if (image.empty()) {
            println("Error: Image not loaded properly!")
            return
        }

        // 가우시안 블러 적용 (커널 크기 축소)
        val blurred = Mat()
        GaussianBlur(image, blurred, Size(1, 1), 0.0)

        // CLAHE 적용 (Contrast Enhancement)
        val clahe = createCLAHE()
        clahe.setClipLimit(2.0) // 너무 강하지 않게 설정
        val enhanced = Mat()
        clahe.apply(blurred, enhanced)

        // 대비 및 밝기 조절 (과도한 조절 방지)
        enhanced.convertTo(enhanced, -1, 1.08, 4.0)

        // 수동 임계값 적용 (140으로 조정)
        val binary = Mat()
        threshold(enhanced, binary, 120.0, 255.0, THRESH_BINARY)

        // 필요 시 색상 반전
        // bitwise_not(binary, binary)

        imwrite(outputPath, binary)

        // 메모리 해제
        image.release()
        blurred.release()
        enhanced.release()
        binary.release()
    }
}