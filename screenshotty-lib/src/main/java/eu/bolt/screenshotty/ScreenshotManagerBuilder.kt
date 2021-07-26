package eu.bolt.screenshotty

import android.app.Activity
import eu.bolt.screenshotty.internal.ScreenshotManagerImpl

class ScreenshotManagerBuilder(private val activity: Activity) {

    private val fallbackStrategies = ArrayList<FallbackStrategy>()
    private var permissionRequestCode = DEFAULT_REQUEST_CODE
    private var actionsOrder: Set<ScreenshotActionOrder> = ScreenshotActionOrder.pixelCopyFirst()

    fun addFallbackStrategy(strategy: FallbackStrategy) = apply {
        fallbackStrategies.add(strategy)
    }

    fun withPermissionRequestCode(code: Int) = apply {
        permissionRequestCode = code
    }

    fun withCustomActionOrder(orders: Set<ScreenshotActionOrder>) = apply {
        require(orders.isNotEmpty()) { "order set cannot be empty" }
        actionsOrder = orders
    }

    fun build(): ScreenshotManager = ScreenshotManagerImpl(
        activity = activity,
        fallbackStrategies = fallbackStrategies,
        permissionRequestCode = permissionRequestCode,
        actionsOrder = actionsOrder
    )

    companion object {
        private const val DEFAULT_REQUEST_CODE = 888
    }
}