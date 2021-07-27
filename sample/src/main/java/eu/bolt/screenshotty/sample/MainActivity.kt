package eu.bolt.screenshotty.sample

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eu.bolt.screenshotty.Screenshot
import eu.bolt.screenshotty.ScreenshotActionOrder
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.ScreenshotManagerBuilder
import eu.bolt.screenshotty.rx.asRxScreenshotManager
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val screenshotManager by lazy {
        ScreenshotManagerBuilder(this)
            .withPermissionRequestCode(REQUEST_SCREENSHOT_PERMISSION)
            .withCustomActionOrder(ScreenshotActionOrder.pixelCopyFirst())
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
                ::handleScreenshot,
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

    private fun handleScreenshot(screenshot: Screenshot) {
        val bitmap = when (screenshot) {
            is ScreenshotBitmap -> screenshot.bitmap
        }
        screenshotPreview.setImageBitmap(bitmap)
        saveScreenShot(bitmap)
    }

    /**
     * If you want to pull the file to pc just use following command
     * adb pull /data/data/eu.bolt.screenshotty/files/file.png ~/Desktop/screenshot.png
     */
    private fun saveScreenShot(bitmap: Bitmap) {
        val file = File(filesDir, "screenshot.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    private fun handleScreenshotError(t: Throwable) {
        Log.e(javaClass.simpleName, t.message, t)
        Toast.makeText(this, t.message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_SCREENSHOT_PERMISSION = 1234
    }
}
