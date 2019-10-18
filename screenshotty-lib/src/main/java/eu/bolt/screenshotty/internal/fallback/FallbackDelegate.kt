package eu.bolt.screenshotty.internal.fallback

import android.app.Activity
import eu.bolt.screenshotty.FallbackStrategy
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.ScreenshotResult
import eu.bolt.screenshotty.internal.ScreenshotResultImpl
import eu.bolt.screenshotty.internal.Utils
import eu.bolt.screenshotty.util.MakeScreenshotFailedException
import java.lang.ref.WeakReference

internal class FallbackDelegate(activity: Activity, strategies: List<FallbackStrategy>) {

    private val activityRef = WeakReference(activity)
    private val strategies = strategies.plus(DefaultFallbackStrategy())

    fun makeScreenshot(): ScreenshotResult {
        val activity = activityRef.get()
        if (activity == null) {
            val error = MakeScreenshotFailedException.noActivityReference()
            return ScreenshotResultImpl.error(error)
        }
        val fallbackBitmap = strategies.asSequence()
            .map { it.tryTakeScreenshot(activity) }
            .filterNotNull()
            .firstOrNull()
        return if (fallbackBitmap != null) {
            val screenshot = ScreenshotBitmap(fallbackBitmap)
            ScreenshotResultImpl.success(screenshot)
        } else {
            val error = MakeScreenshotFailedException.fallbackStrategiesFailed()
            ScreenshotResultImpl.error(error)
        }
    }

    private fun FallbackStrategy.tryTakeScreenshot(activity: Activity) = try {
        takeScreenshot(activity)
    } catch (e: Exception) {
        Utils.logE(e)
        null
    }
}