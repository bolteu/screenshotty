package eu.bolt.screenshotty.rx

import android.content.Intent
import eu.bolt.screenshotty.Screenshot
import io.reactivex.Single

interface RxScreenshotManager {

    fun makeScreenshot(): Single<Screenshot>

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}