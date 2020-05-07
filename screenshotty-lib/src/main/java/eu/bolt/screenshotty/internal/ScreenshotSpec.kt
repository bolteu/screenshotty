package eu.bolt.screenshotty.internal

import android.app.Activity
import android.graphics.Point
import android.os.Build
import android.view.Display

internal class ScreenshotSpec constructor(activity: Activity) {

    val width: Int
    val height: Int
    val densityDpi: Int

    init {
        val display = activity.windowManager.defaultDisplay
        val displaySize = getDisplaySize(display)
        width = displaySize.x
        height = displaySize.y
        densityDpi = activity.resources.displayMetrics.densityDpi
    }

    private fun getDisplaySize(display: Display): Point {
        return Point().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(this)
            } else {
                display.getSize(this)
            }
        }
    }
}