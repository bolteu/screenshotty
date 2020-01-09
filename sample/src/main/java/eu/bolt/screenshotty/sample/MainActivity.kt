package eu.bolt.screenshotty.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cube.Cube
import embeddedActivity.EmbeddedActivityHost
import eu.bolt.screenshotty.Screenshot
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.ScreenshotManagerBuilder
import eu.bolt.screenshotty.rx.asRxScreenshotManager
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var host = EmbeddedActivityHost(this)

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

        (findViewById<View>(R.id.View) as FrameLayout).addView(host.inflate(R.layout.fragment_container))
        host.bindId(R.id.fragment_container)
        host.log.log("savedInstanceState is $savedInstanceState")
        if (savedInstanceState == null) {
            host.addAndBuildClient(R.id.fragment_container, Cube())
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
