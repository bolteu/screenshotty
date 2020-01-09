package cube;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class NativeView {
    String TAG = "EglSample";

    public final long instance;
    public final native long nativeNewInstance();
    public final native void nativeOnCreate(long instance);
    public final native void nativeOnStart(long instance);
    public final native void nativeOnResume(long instance);
    public final native void nativeOnPause(long instance);
    public final native void nativeOnStop(long instance);
    public final native void nativeOnDestroy(long instance);
    public final native void nativeDeleteInstance(long instance);
    public final native void nativeSetSurface(long instance, Surface surface);

    View surfaceView;
    SurfaceHolderCallback surfaceHolderCallback;

    public NativeView(Context context) {
        System.loadLibrary("nativeegl");
        instance = nativeNewInstance();
        surfaceHolderCallback = new SurfaceHolderCallback(instance);
        surfaceView = new View(surfaceHolderCallback, context);
    }

    class View extends SurfaceView {
        public View(SurfaceHolder.Callback callback, Context context) {
            super(context);
            getHolder().addCallback(callback);
        }

        public View(SurfaceHolder.Callback callback, Context context, AttributeSet attrs) {
            super(context, attrs);
            getHolder().addCallback(callback);
        }

        public View(SurfaceHolder.Callback callback, Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            getHolder().addCallback(callback);
        }

        public View(SurfaceHolder.Callback callback, Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
            super(context, attrs, defStyle, defStyleRes);
            getHolder().addCallback(callback);
        }
    }

    class SurfaceHolderCallback implements SurfaceHolder.Callback {
        long mInstance;
        public SurfaceHolderCallback(long instance) {
            mInstance = instance;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            nativeSetSurface(mInstance, holder.getSurface());
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            nativeSetSurface(mInstance, null);
        }
    }
}
