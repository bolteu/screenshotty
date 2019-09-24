package eu.bolt.screenshotty.internal

import android.annotation.SuppressLint
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Build
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi

internal object Utils {

    private const val LOG_TAG = "screenshot"

    private const val LOG_ENABLED = false

    fun checkOnMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw IllegalAccessException("The method can be called only on the main thread")
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun closeSafely(image: Image?) = doSafely {
        image?.close()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun closeSafely(reader: ImageReader?) = doSafely {
        reader?.close()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun stopSafely(projection: MediaProjection?) = doSafely {
        projection?.stop()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun releaseSafely(display: VirtualDisplay?) = doSafely {
        display?.release()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun interruptSafely(thread: HandlerThread) = doSafely {
        thread.quitSafely()
        thread.interrupt()
    }

    private inline fun doSafely(action: () -> Unit) = try {
        action()
    } catch (e: Exception) {
        logE(e)
    }

    @SuppressLint("LogNotTimber")
    fun logE(throwable: Throwable) {
        if (LOG_ENABLED) {
            Log.e(LOG_TAG, throwable.message, throwable)
        }
    }

    @SuppressLint("LogNotTimber")
    fun logE(message: String) {
        if (LOG_ENABLED) {
            Log.e(LOG_TAG, message)
        }
    }
}