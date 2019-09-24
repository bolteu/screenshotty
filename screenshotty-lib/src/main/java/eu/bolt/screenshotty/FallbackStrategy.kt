package eu.bolt.screenshotty

import android.app.Activity
import android.graphics.Bitmap

interface FallbackStrategy {
    fun takeScreenshot(activity: Activity): Bitmap
}