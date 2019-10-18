package eu.bolt.screenshotty.internal

import android.app.Activity
import android.content.Intent
import android.os.Build
import eu.bolt.screenshotty.FallbackStrategy
import eu.bolt.screenshotty.ScreenshotManager
import eu.bolt.screenshotty.ScreenshotResult
import eu.bolt.screenshotty.internal.fallback.FallbackDelegate
import eu.bolt.screenshotty.internal.pixelcopy.PixelCopyDelegateCompat
import eu.bolt.screenshotty.internal.pixelcopy.PixelCopyDelegateV26
import eu.bolt.screenshotty.internal.projection.MediaProjectionDelegateCompat
import eu.bolt.screenshotty.internal.projection.MediaProjectionDelegateV21

internal class ScreenshotManagerImpl(
    activity: Activity,
    fallbackStrategies: List<FallbackStrategy>,
    permissionRequestCode: Int
) : ScreenshotManager {

    private val pixelCopyDelegate = createPixelCopyDelegate(activity)
    private val mediaProjectionDelegate = createMediaProjectionDelegate(activity, permissionRequestCode)
    private val fallbackDelegate = FallbackDelegate(activity, fallbackStrategies)

    override fun makeScreenshot(): ScreenshotResult {
        return ScreenshotResultImpl.from(pixelCopyDelegate.makeScreenshot())
            .onErrorFallbackTo { mediaProjectionDelegate.makeScreenshot() }
            .onErrorFallbackTo { fallbackDelegate.makeScreenshot() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mediaProjectionDelegate.onActivityResult(requestCode, resultCode, data)
    }

    private fun createPixelCopyDelegate(activity: Activity) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PixelCopyDelegateV26(activity)
        } else {
            PixelCopyDelegateCompat()
        }

    private fun createMediaProjectionDelegate(activity: Activity, permissionRequestCode: Int) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaProjectionDelegateV21(activity, permissionRequestCode)
        } else {
            MediaProjectionDelegateCompat()
        }

}