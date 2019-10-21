package eu.bolt.screenshotty.internal

import android.view.View
import android.view.ViewTreeObserver

internal fun View.doOnPreDraw(dropFrame: Boolean, action: () -> Unit) {
    val observer = viewTreeObserver
    observer.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            if (observer.isAlive) {
                observer.removeOnPreDrawListener(this)
            }
            action()
            return !dropFrame
        }
    })
    postInvalidate()
}