package eu.bolt.screenshotty.internal

import android.app.Activity
import android.content.Intent
import android.os.Build
import eu.bolt.screenshotty.FallbackStrategy
import eu.bolt.screenshotty.ScreenshotActionOrder
import eu.bolt.screenshotty.ScreenshotManager
import eu.bolt.screenshotty.ScreenshotResult
import eu.bolt.screenshotty.internal.fallback.FallbackDelegate
import eu.bolt.screenshotty.internal.floatingpanel.FloatingPanelDataProvider
import eu.bolt.screenshotty.internal.floatingpanel.FloatingPanelRenderer
import eu.bolt.screenshotty.internal.pixelcopy.PixelCopyDelegateCompat
import eu.bolt.screenshotty.internal.pixelcopy.PixelCopyDelegateV26
import eu.bolt.screenshotty.internal.projection.MediaProjectionDelegateCompat
import eu.bolt.screenshotty.internal.projection.MediaProjectionDelegateV21

internal class ScreenshotManagerImpl(
        activity: Activity,
        fallbackStrategies: List<FallbackStrategy>,
        permissionRequestCode: Int,
        private val actionsOrder: Set<ScreenshotActionOrder>
) : ScreenshotManager {

    private val panelRenderer = FloatingPanelRenderer(FloatingPanelDataProvider.getInstance())
    private val pixelCopyDelegate = createPixelCopyDelegate(activity, panelRenderer)
    private val mediaProjectionDelegate = createMediaProjectionDelegate(activity, permissionRequestCode)
    private val fallbackDelegate = FallbackDelegate(activity, fallbackStrategies, panelRenderer)

    override fun makeScreenshot(): ScreenshotResult = actionsOrder.drop(1)
            .fold(ScreenshotResultImpl.from(mapOrderToAction(actionsOrder.first())()))
            { stream, order -> stream.onErrorFallbackTo(mapOrderToAction(order)) }

    private fun mapOrderToAction(order: ScreenshotActionOrder) = when (order) {
        ScreenshotActionOrder.Fallbacks -> fallbackDelegate::makeScreenshot
        ScreenshotActionOrder.MediaProjection -> mediaProjectionDelegate::makeScreenshot
        ScreenshotActionOrder.PixelCopy -> pixelCopyDelegate::makeScreenshot
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mediaProjectionDelegate.onActivityResult(requestCode, resultCode, data)
    }

    private fun createPixelCopyDelegate(activity: Activity, panelRenderer: FloatingPanelRenderer) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PixelCopyDelegateV26(activity, panelRenderer)
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