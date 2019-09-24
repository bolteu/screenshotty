package eu.bolt.screenshotty.internal.projection

import android.content.Intent
import eu.bolt.screenshotty.ScreenshotResult

internal interface MediaProjectionDelegate {

    fun makeScreenshot(): ScreenshotResult

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}