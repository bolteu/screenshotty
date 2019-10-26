package eu.bolt.screenshotty.internal.floatingpanel

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.CheckResult
import eu.bolt.screenshotty.internal.Utils

internal class FloatingPanelRenderer(private val windowDataProvider: FloatingPanelDataProvider) {

    @CheckResult
    fun tryRenderDialogs(activity: Activity, original: Bitmap): Bitmap {
        val panels = windowDataProvider.getFloatingPanels(activity)

        if (panels.isEmpty()) {
            return original
        }

        return try {
            val canvas = Canvas(original)

            panels.asSequence()
                .filterNot { it.isDialog() }
                .forEach { drawPanel(canvas, it) }

            val dialog = panels.find { it.isDialog() }
            if (dialog != null) {
                val dimColorAlpha = (dialog.layoutParams.dimAmount * 255).toInt()
                canvas.drawColor(Color.argb(dimColorAlpha, 0, 0, 0))
                drawPanel(canvas, dialog)
            }

            original
        } catch (e: Exception) {
            Utils.logE(e)
            original
        }
    }

    private fun drawPanel(canvas: Canvas, info: FloatingPanelData) {
        val panelView = info.rootView
        if (panelView.width == 0 || panelView.height == 0) {
            return
        }
        canvas.save()
        canvas.translate(info.windowFrame.left.toFloat(), info.windowFrame.top.toFloat())
        info.rootView.draw(canvas)
        canvas.restore()
    }

}