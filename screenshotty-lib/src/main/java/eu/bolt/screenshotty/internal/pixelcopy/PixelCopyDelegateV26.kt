package eu.bolt.screenshotty.internal.pixelcopy

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.ScreenshotResult
import eu.bolt.screenshotty.internal.ScreenshotResultImpl
import eu.bolt.screenshotty.internal.ScreenshotSpec
import eu.bolt.screenshotty.internal.Utils.checkOnMainThread
import eu.bolt.screenshotty.internal.doOnPreDraw
import eu.bolt.screenshotty.util.MakeScreenshotFailedException
import java.lang.Exception
import java.lang.ref.WeakReference

@TargetApi(Build.VERSION_CODES.O)
class PixelCopyDelegateV26(activity: Activity) : PixelCopyDelegate {

    private val activityReference = WeakReference(activity)
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private var pendingResult: ScreenshotResultImpl? = null

    override fun makeScreenshot(): ScreenshotResult {
        checkOnMainThread()
        val result = pendingResult
        if (result != null) {
            return result
        }
        val activity = activityReference.get()
        if (activity == null) {
            val exception = MakeScreenshotFailedException.noActivityReference()
            return ScreenshotResultImpl.error(exception)
        }
        val screenshotSpec = ScreenshotSpec(activity)
        val newResult = ScreenshotResultImpl(screenshotSpec)
        makePixelCopyWhenDrawn(activity, screenshotSpec)
        pendingResult = newResult
        return newResult
    }

    private fun makePixelCopyWhenDrawn(activity: Activity, spec: ScreenshotSpec) {
        activity.window.decorView.doOnPreDraw(dropFrame = false) {
            mainThreadHandler.post {
                doMakePixelCopy(activity, spec)
            }
        }
    }

    private fun doMakePixelCopy(activity: Activity, spec: ScreenshotSpec) {
        val window = activity.window
        try {
            val copyDestination = Bitmap.createBitmap(spec.width, spec.height, Bitmap.Config.ARGB_8888)
            PixelCopy.request(window, copyDestination, { onPixelCopyTaken(it, copyDestination) }, mainThreadHandler)
        } catch (e: Exception) {
            onPixelCopyFailed(e)
        }
    }

    private fun onPixelCopyTaken(resultCode: Int, bitmap: Bitmap) {
        checkOnMainThread()
        val result = pendingResult ?: return
        if (resultCode == PixelCopy.SUCCESS) {
            result.onSuccess(ScreenshotBitmap(bitmap))
        } else {
            val error = MakeScreenshotFailedException.pixelCopyFailed(resultCode)
            result.onError(error)
        }
        pendingResult = null
    }

    private fun onPixelCopyFailed(error: Exception) {
        checkOnMainThread()
        pendingResult?.let {
            it.onError(error)
            pendingResult = null
        }
    }
}