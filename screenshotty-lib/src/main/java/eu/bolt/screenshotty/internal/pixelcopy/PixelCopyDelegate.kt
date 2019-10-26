package eu.bolt.screenshotty.internal.pixelcopy

import eu.bolt.screenshotty.ScreenshotResult

internal interface PixelCopyDelegate {

    fun makeScreenshot(): ScreenshotResult

}