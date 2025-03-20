package app.utils

import org.opencv.core.Core
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

object ImagePreprocessor {
    fun preprocessImage(inputPath: String, outputPath: String) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val image = Imgcodecs.imread(inputPath, Imgcodecs.IMREAD_GRAYSCALE)

        // 가우시안 블러 적용 (노이즈 제거)
        Imgproc.GaussianBlur(image, image, Size(5.0, 5.0), 0.0)

        // 대비 조절
        Core.multiply(image, Scalar(1.5), image)

        // OTSU 이진화 적용
        Imgproc.threshold(image, image, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)

        // 이미지 저장
        Imgcodecs.imwrite(outputPath, image)
    }
}
