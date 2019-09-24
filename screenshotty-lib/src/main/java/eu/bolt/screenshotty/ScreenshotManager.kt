package eu.bolt.screenshotty

import android.content.Intent

interface ScreenshotManager {

    fun makeScreenshot(): ScreenshotResult

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}