# Screenshotty

The library combines [MediaProjection](https://developer.android.com/reference/android/media/projection/MediaProjection), [PixelCopy](https://developer.android.com/reference/android/view/PixelCopy) and Canvas drawing and provides an easy-to-use API, abstracted from the Android framework and the complexities of the underlying mechanisms, to capture precisely what a user sees on their screen.

[The sample app](https://github.com/bolteu/screenshotty/blob/master/sample/src/main/java/eu/bolt/screenshotty/sample/MainActivity.kt) shows how to use the library.

## Gradle
Add this to your dependencies block.
```
implementation 'eu.bolt:screenshotty:1.0.2'
```

To use a [reactive wrapper](https://github.com/bolteu/screenshotty/new/master?readme=1#reactive-wrapper) also add:
```
implementation 'eu.bolt:screenshotty-rx:1.0.2'
```

## Wiki

### General
If we want to capture a screenshot inside the app, the simplest approach is to draw the root view on a `Bitmap`, but
this approach won't work correctly if there are open dialogs, or view hierarchy contains maps or other `SurfaceView`s.
Screenshotty uses [PixelCopy](https://developer.android.com/reference/android/view/PixelCopy) and [MediaProjection](https://developer.android.com/reference/android/media/projection/MediaProjection) to
provide the correct image in all these cases.

First the library tries to make a `PixelCopy` with dialogs, retrieved via reflection, rendered on top.

If this approach fails, user will see a record screen permission dialog. Screenshotty minimizes the number of times the dialog is shown: permission has to be granted only once per process lifetime. If "Don't show again" option (removed in Android 10) is checked, the system will remember user's choice for all the future invocations. If the permission is granted, a `MediaProjection` API is used to take a single frame and provide it to result listeners.

In case `MediaProjection` fails, fallback strategies are invoked one-by-one until the first one succeeds to provide a Bitmap.

### Usage

1. Create a `ScreenshotManager`:

```kotlin
screenshotManager = ScreenshotManagerBuilder(this)
   .withPermissionRequestCode(REQUEST_SCREENSHOT_PERMISSION) //optional, 888 is the default
   .build()
```

2. Make sure the object receives activity results:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
   super.onActivityResult(requestCode, resultCode, data)
   screenshotManager.onActivityResult(requestCode, resultCode, data)
}
```
3. Request a screenshot and observe the result:

```kotlin
val screenshotResult = screenshotManager.makeScreenshot()
val subscription = screenshotResult.observe(
   onSuccess = { processScreenshot(it) },
   onError = { onMakeScreenshotFailed(it) }
)
```
4. If you're no longer interested in the result (as when your `Activity` is destroyed), you can unsubscribe your observers using the object you got from `observe()`.

```kotlin
override fun onDestroy() {
   super.onDestroy()
   subscription.dispose()
}
```

### Working with result

When you receive a `Screenshot` you can either get a `Bitmap` object from it:
```kotlin
fun show(screenshot: Screenshot) {
   val bitmap = when (screenshot) {
      is ScreenshotBitmap -> screenshot.bitmap
   }
   screenshotPreview.setImageBitmap(bitmap)
}
```
Or use `ScreenshotFileSaver` provided by the library to write the image to a file:
```kotlin
fun writeToFile(screenshot: Screenshot): File {
   val fileSaver = ScreenshotFileSaver.create(Bitmap.CompressFormat.PNG)
   val targetFile = File(context.filesDir, "screenshot")
   fileSaver.saveToFile(targetFile, screenshot)
   return targetFile
}
```

### Reactive wrapper

If you're using [screenshotty-rx](https://github.com/bolteu/screenshotty/new/master?readme=1#gradle), you can transform your `ScreenshotManager` object into `RxScreenshotManager`:
```kotlin
val rxScreenshotManager = screenshotManager.asRxScreenshotManager() //or RxScreenshotWrapper.wrap(screenshotManager)
```
[Usage](https://github.com/bolteu/screenshotty/new/master?readme=1#usage) is exactly the same, but `makeScreenshot()` returns `Single<Screenshot>` instead of `ScreenshotResult`, so you can use all the expressive power of reactive composition to process the result:
```kotlin
subscription = rxScreenshotManager.makeScreenshot()
   .observeOn(Schedulers.io())
   .map(::writeToFile)
   .doOnSuccess(::sendScreenshotFile)
   .observeOn(AndroidSchedulers.mainThread())
   .subscribe(
      onSuccess = ::onScreenshotSent,
      onError = ::handleError
   )
```

### Fallback strategies

When constructing a `ScreenshotManager` you can add any number of objects that implement [`FallbackStrategy`](https://github.com/bolteu/screenshotty/blob/master/screenshotty-lib/src/main/java/eu/bolt/screenshotty/FallbackStrategy.kt) interface. If `PixelCopy` or `MediaProjection` fails for some reason, fallback strategies will be invoked
one by one in the order they were added, until the first one succeeds to provide a `Bitmap`.

If no strategies were added or all of them failed, the default one (that simply calls `draw` on the root view and tries to render dialogs retrieved via reflection on top) will be invoked.

## License
```
MIT License

Copyright (c) 2020 Bolt Technologies OÜ

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
