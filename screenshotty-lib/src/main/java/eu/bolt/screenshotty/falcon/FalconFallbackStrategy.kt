package eu.bolt.screenshotty.falcon

import android.app.Activity
import android.graphics.Bitmap
import eu.bolt.screenshotty.FallbackStrategy

class FalconFallbackStrategy : FallbackStrategy {
    override fun takeScreenshot(activity: Activity): Bitmap = Falcon.takeScreenshotBitmap(activity)
}