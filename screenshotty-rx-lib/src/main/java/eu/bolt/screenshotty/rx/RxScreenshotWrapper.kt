package eu.bolt.screenshotty.rx

import eu.bolt.screenshotty.ScreenshotManager

object RxScreenshotWrapper {

    @JvmStatic
    fun wrap(manager: ScreenshotManager): RxScreenshotManager = RxScreenshotManagerImpl(manager)

}

fun ScreenshotManager.asRxScreenshotManager(): RxScreenshotManager = RxScreenshotManagerImpl(this)