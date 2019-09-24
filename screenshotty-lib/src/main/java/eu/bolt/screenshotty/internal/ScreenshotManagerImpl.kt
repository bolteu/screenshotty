package eu.bolt.screenshotty.internal

import android.app.Activity
import android.content.Intent
import android.os.Build
import eu.bolt.screenshotty.ScreenshotManager
import eu.bolt.screenshotty.ScreenshotResult
import eu.bolt.screenshotty.FallbackStrategy
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.internal.projection.MediaProjectionDelegateCompat
import eu.bolt.screenshotty.internal.projection.MediaProjectionDelegateV21
import eu.bolt.screenshotty.internal.Utils.logE
import java.lang.ref.WeakReference

internal class ScreenshotManagerImpl(
    activity: Activity,
    fallbackStrategies: List<FallbackStrategy>,
    permissionRequestCode: Int
) : ScreenshotManager {

    private val activityRef = WeakReference(activity)
    private val mediaProjectionDelegate = createMediaProjectionDelegate(activity, permissionRequestCode)
    private val fallbackStrategies = fallbackStrategies.plus(DefaultFallbackStrategy())

    override fun makeScreenshot(): ScreenshotResult {
        val result = ScreenshotResultImpl(null)
        val mediaProjectionResult = mediaProjectionDelegate.makeScreenshot()
        mediaProjectionResult.observe(result::onSuccess, createFallbackHandler(result))
        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mediaProjectionDelegate.onActivityResult(requestCode, resultCode, data)
    }

    private fun createFallbackHandler(result: ScreenshotResultImpl): (Throwable) -> Unit = { error ->
        val activity = activityRef.get()
        if (activity == null) {
            result.onError(error)
        } else {
            val fallbackBitmap = fallbackStrategies.asSequence()
                .map { it.tryTakeScreenshot(activity) }
                .filterNotNull()
                .firstOrNull()
            if (fallbackBitmap != null) {
                result.onSuccess(ScreenshotBitmap(fallbackBitmap))
            } else {
                result.onError(error)
            }
        }
    }

    private fun FallbackStrategy.tryTakeScreenshot(activity: Activity) = try {
        takeScreenshot(activity)
    } catch (e: Exception) {
        logE(e)
        null
    }

    private fun createMediaProjectionDelegate(activity: Activity, permissionRequestCode: Int) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaProjectionDelegateV21(activity, permissionRequestCode)
        } else {
            MediaProjectionDelegateCompat()
        }

}