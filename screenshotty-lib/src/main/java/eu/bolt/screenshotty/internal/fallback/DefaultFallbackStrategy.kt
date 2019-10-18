package eu.bolt.screenshotty.internal.fallback

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import eu.bolt.screenshotty.FallbackStrategy
import eu.bolt.screenshotty.internal.ScreenshotSpec

internal class DefaultFallbackStrategy : FallbackStrategy {

    override fun takeScreenshot(activity: Activity): Bitmap {
        val spec = ScreenshotSpec(activity)
        val content = activity.findViewById<View>(android.R.id.content)
        val bitmap = Bitmap.createBitmap(spec.width, spec.height, Bitmap.Config.ARGB_8888)
        content.draw(Canvas(bitmap))
        return bitmap
    }
}