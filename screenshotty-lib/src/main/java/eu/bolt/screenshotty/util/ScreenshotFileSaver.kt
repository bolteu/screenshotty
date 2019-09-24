package eu.bolt.screenshotty.util

import android.graphics.Bitmap
import androidx.annotation.IntRange
import eu.bolt.screenshotty.Screenshot
import eu.bolt.screenshotty.ScreenshotBitmap
import java.io.File
import java.io.FileOutputStream

class ScreenshotFileSaver private constructor(
    private val compressFormat: Bitmap.CompressFormat,
    private val compressQuality: Int
) {

    fun saveToFile(targetFile: File, screenshot: Screenshot) {
        val bitmap = screenshotToBitmap(screenshot)
        if (!targetFile.exists()) {
            targetFile.createNewFile()
        }
        bitmap.compress(compressFormat, compressQuality, FileOutputStream(targetFile))
    }

    private fun screenshotToBitmap(screenshot: Screenshot) = when (screenshot) {
        is ScreenshotBitmap -> screenshot.bitmap
    }

    companion object {
        const val QUALITY_BEST = 100

        fun create(
            compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
            @IntRange(from = 0, to = QUALITY_BEST.toLong()) compressQuality: Int = QUALITY_BEST
        ) = ScreenshotFileSaver(compressFormat, compressQuality)
    }

}