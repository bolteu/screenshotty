package eu.bolt.screenshotty.internal.pixelcopy

import eu.bolt.screenshotty.ScreenshotResult
import eu.bolt.screenshotty.internal.ScreenshotResultImpl
import eu.bolt.screenshotty.util.MakeScreenshotFailedException

internal class PixelCopyDelegateCompat : PixelCopyDelegate {

    override fun makeScreenshot(): ScreenshotResult {
        val exception = MakeScreenshotFailedException.pixelCopyNotSupported()
        return ScreenshotResultImpl.error(exception)
    }

}