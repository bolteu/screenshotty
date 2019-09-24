package eu.bolt.screenshotty.internal.projection

import android.content.Intent
import eu.bolt.screenshotty.ScreenshotResult
import eu.bolt.screenshotty.internal.ScreenshotResultImpl
import eu.bolt.screenshotty.util.MakeScreenshotFailedException

internal class MediaProjectionDelegateCompat : MediaProjectionDelegate {

    override fun makeScreenshot(): ScreenshotResult {
        val exception = MakeScreenshotFailedException.projectionNotSupported()
        return ScreenshotResultImpl.error(exception)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //NOP
    }

}