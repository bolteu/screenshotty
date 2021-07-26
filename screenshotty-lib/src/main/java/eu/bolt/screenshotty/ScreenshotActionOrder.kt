package eu.bolt.screenshotty

sealed class ScreenshotActionOrder {
    object PixelCopy : ScreenshotActionOrder()
    object MediaProjection : ScreenshotActionOrder()
    object Fallbacks : ScreenshotActionOrder();

    companion object {
        fun pixelCopyFirst() = setOf(PixelCopy, MediaProjection, Fallbacks)
        fun mediaProjectionFirst() = setOf(MediaProjection, PixelCopy, Fallbacks)
        fun fallbacksFirst() = setOf(Fallbacks, PixelCopy, MediaProjection)
    }
}