package eu.bolt.screenshotty.rx

import android.content.Intent
import eu.bolt.screenshotty.Screenshot
import eu.bolt.screenshotty.ScreenshotManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

internal class RxScreenshotManagerImpl(
    private val screenshotManager: ScreenshotManager
) : RxScreenshotManager {

    override fun makeScreenshot(): Single<Screenshot> {
        return Single.create<Screenshot> { s ->
            val result = screenshotManager.makeScreenshot()
            result.observe(s::onSuccess, s::onError)
        }.subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        screenshotManager.onActivityResult(requestCode, resultCode, data)
    }

}