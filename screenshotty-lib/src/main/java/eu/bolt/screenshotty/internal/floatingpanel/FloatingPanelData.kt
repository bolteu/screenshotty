package eu.bolt.screenshotty.internal.floatingpanel

import android.graphics.Rect
import android.view.View
import android.view.WindowManager

internal class FloatingPanelData(
    val rootView: View,
    val windowFrame: Rect,
    val layoutParams: WindowManager.LayoutParams) {

    fun isDialog() = layoutParams.type == WindowManager.LayoutParams.TYPE_APPLICATION
}
