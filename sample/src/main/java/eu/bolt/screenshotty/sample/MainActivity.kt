package eu.bolt.screenshotty.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import eu.bolt.screenshotty.Screenshot
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.ScreenshotManagerBuilder
import eu.bolt.screenshotty.rx.asRxScreenshotManager
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_main.screenshotPreview
import kotlinx.android.synthetic.main.activity_main.shotButton

class MainActivity : AppCompatActivity() {

    private val screenshotManager by lazy {
        ScreenshotManagerBuilder(this)
            .withPermissionRequestCode(REQUEST_SCREENSHOT_PERMISSION)
            .build()
            .asRxScreenshotManager()
    }

    private var screenshotSubscription = Disposables.disposed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shotButton.setOnClickListener {
            makeScreenshot()
        }
    }

    private fun makeScreenshot() {
        screenshotSubscription.dispose()
        screenshotSubscription = screenshotManager
            .makeScreenshot()
            .subscribe(
                ::showScreenshot,
                ::handleScreenshotError
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        screenshotSubscription.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        screenshotManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun showScreenshot(screenshot: Screenshot) {
        val bitmap = when (screenshot) {
            is ScreenshotBitmap -> screenshot.bitmap
        }
        screenshotPreview.setImageBitmap(bitmap)
    }

    private fun handleScreenshotError(t: Throwable) {
        Log.e(javaClass.simpleName, t.message, t)
        Toast.makeText(this, t.message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_SCREENSHOT_PERMISSION = 1234
    }
}
