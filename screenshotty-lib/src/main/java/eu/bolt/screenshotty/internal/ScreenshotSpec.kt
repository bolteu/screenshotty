package eu.bolt.screenshotty.internal

import android.app.Activity
import android.graphics.Point

internal class ScreenshotSpec constructor(activity: Activity) {

    val width: Int
    val height: Int
    val densityDpi: Int

    init {
        val display = activity.windowManager.defaultDisplay
        val displaySize = Point().apply(display::getSize)
        width = displaySize.x
        height = displaySize.y
        densityDpi = activity.resources.displayMetrics.densityDpi
    }
}