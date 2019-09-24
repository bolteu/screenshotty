package eu.bolt.screenshotty

import android.app.Activity
import eu.bolt.screenshotty.internal.ScreenshotManagerImpl

class ScreenshotManagerBuilder(private val activity: Activity) {

    private val fallbackStrategies = ArrayList<FallbackStrategy>()
    private var permissionRequestCode = DEFAULT_REQUEST_CODE

    fun addFallbackStrategy(strategy: FallbackStrategy) = apply {
        fallbackStrategies.add(strategy)
    }

    fun withPermissionRequestCode(code: Int) = apply {
        permissionRequestCode = code
    }

    fun build(): ScreenshotManager = ScreenshotManagerImpl(
        activity = activity,
        fallbackStrategies = fallbackStrategies,
        permissionRequestCode = permissionRequestCode
    )

    companion object {
        private const val DEFAULT_REQUEST_CODE = 888
    }
}