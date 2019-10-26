package eu.bolt.screenshotty.internal.fallback

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import eu.bolt.screenshotty.FallbackStrategy
import eu.bolt.screenshotty.internal.ScreenshotSpec
import eu.bolt.screenshotty.internal.floatingpanel.FloatingPanelRenderer

internal class DefaultFallbackStrategy(
    private val floatingPanelRenderer: FloatingPanelRenderer
) : FallbackStrategy {

    override fun takeScreenshot(activity: Activity): Bitmap {
        val spec = ScreenshotSpec(activity)
        val decorView = activity.window.decorView
        val bitmap = Bitmap.createBitmap(spec.width, spec.height, Bitmap.Config.ARGB_8888)
        decorView.draw(Canvas(bitmap))
        return floatingPanelRenderer.tryRenderDialogs(activity, bitmap)
    }
}