package eu.bolt.screenshotty

import android.graphics.Bitmap

sealed class Screenshot
class ScreenshotBitmap(val bitmap: Bitmap) : Screenshot()