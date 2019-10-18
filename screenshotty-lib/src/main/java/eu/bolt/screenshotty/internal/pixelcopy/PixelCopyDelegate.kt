package eu.bolt.screenshotty.internal.pixelcopy

import eu.bolt.screenshotty.ScreenshotResult

interface PixelCopyDelegate {

    fun makeScreenshot(): ScreenshotResult

}