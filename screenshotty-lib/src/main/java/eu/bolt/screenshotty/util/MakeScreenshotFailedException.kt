package eu.bolt.screenshotty.util

import java.lang.RuntimeException

class MakeScreenshotFailedException : RuntimeException {

    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)

    internal companion object {
        fun noActivityReference() = MakeScreenshotFailedException("Can't make a screenshot because Activity was garbage collected")

        fun failedToCreateMediaProjection() = MakeScreenshotFailedException("Failed to create MediaProjection object")

        fun failedToAcquireImage() = MakeScreenshotFailedException("ImageReader::acquireLatestImage returned null")

        fun projectionNotSupported() = MakeScreenshotFailedException("MediaProjection not supported on this API version")

        fun pixelCopyNotSupported() = MakeScreenshotFailedException("PixelCopy not supported on this API version")

        fun pixelCopyFailed(code: Int) = MakeScreenshotFailedException("PixelCopy failed, result code = $code")

        fun fallbackStrategiesFailed() = MakeScreenshotFailedException("All fallback strategies failed")
    }
}