package eu.bolt.screenshotty.internal

import androidx.annotation.CheckResult
import eu.bolt.screenshotty.Screenshot
import eu.bolt.screenshotty.ScreenshotResult
import eu.bolt.screenshotty.Subscriptions
import eu.bolt.screenshotty.internal.Utils.checkOnMainThread

internal class ScreenshotResultImpl(val spec: ScreenshotSpec? = null) : ScreenshotResult {

    private val subscriptions = ArrayList<SubscriptionImpl>()

    private var deliveredScreenshot: Screenshot? = null
    private var deliveredError: Throwable? = null

    fun onSuccess(screenshot: Screenshot) {
        checkResultNotSet()
        checkOnMainThread()
        deliveredScreenshot = screenshot
        subscriptions.forEach {
            it.onSuccess?.invoke(screenshot)
        }
        subscriptions.clear()
    }

    fun onError(error: Throwable) {
        checkResultNotSet()
        checkOnMainThread()
        deliveredError = error
        subscriptions.forEach {
            it.onError?.invoke(error)
        }
        subscriptions.clear()
    }

    @CheckResult
    fun onErrorFallbackTo(resultProvider: () -> ScreenshotResult): ScreenshotResultImpl {
        val newResult = ScreenshotResultImpl()
        observe(newResult::onSuccess) { error ->
            Utils.logE(error)
            val next = resultProvider()
            next.observe(newResult::onSuccess, newResult::onError)
        }
        return newResult
    }

    override fun observe(onSuccess: (Screenshot) -> Unit, onError: (Throwable) -> Unit): ScreenshotResult.Subscription {
        checkOnMainThread()
        val screenshot = deliveredScreenshot
        val error = deliveredError
        return if (screenshot != null) {
            onSuccess(screenshot)
            Subscriptions.disposed()
        } else if (error != null) {
            onError(error)
            Subscriptions.disposed()
        } else {
            val newSubscription = SubscriptionImpl(onSuccess, onError)
            subscriptions.add(newSubscription)
            newSubscription
        }
    }

    private fun checkResultNotSet() {
        if (deliveredScreenshot != null || deliveredError != null) {
            error("attempted to set ScreenshotResult content multiple times")
        }
    }

    private class SubscriptionImpl(
        var onSuccess: ((Screenshot) -> Unit)?,
        var onError: ((Throwable) -> Unit)?
    ) : ScreenshotResult.Subscription {

        override fun dispose() {
            checkOnMainThread()
            onSuccess = null
            onError = null
        }
    }

    companion object {
        fun success(screenshot: Screenshot) = ScreenshotResultImpl().apply {
            onSuccess(screenshot)
        }

        fun error(e: Throwable) = ScreenshotResultImpl().apply {
            onError(e)
        }

        fun from(another: ScreenshotResult): ScreenshotResultImpl {
            val result = ScreenshotResultImpl()
            another.observe(result::onSuccess, result::onError)
            return result
        }
    }
}