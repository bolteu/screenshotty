package embeddedActivity;

import android.util.Log;

import androidx.annotation.Nullable;

import static org.junit.Assert.assertNotNull;

public class LogUtils {
    private String TAG = "";
    private String ERRORMESSAGE = "An error has occured";

    public LogUtils(String tag) {
        TAG = tag;
    }

    public LogUtils(String tag, String errorMessage) {
        TAG = tag;
        ERRORMESSAGE = errorMessage;
    }

    public final void log(String message) {
        Log.d(TAG, message);
    }

    public final Throwable error() {
        return error(ERRORMESSAGE);
    }

    public final AssertionError error(String message) {
        AssertionError t = new AssertionError(message);
        Log.e(TAG, Log.getStackTraceString(t));
        return t;
    }

    public final void errorNoStackTrace() {
        errorNoStackTrace(ERRORMESSAGE);
    }

    public final void errorNoStackTrace(String message) {
        Log.e(TAG, message);
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorIfNull(@Nullable T object) {
        return errorIfNull(object, ERRORMESSAGE);
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorIfNull(@Nullable T object, String message) {
        if (object == null) error(message);
        return object;
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorIfNullNoStackTrace(@Nullable T object) {
        return errorIfNullNoStackTrace(object, ERRORMESSAGE);
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorIfNullNoStackTrace(@Nullable T object, String message) {
        if (object == null) errorNoStackTrace(message);
        return object;
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorAndThrowIfNull(@Nullable T object) {
        return errorAndThrowIfNull(object, ERRORMESSAGE);
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final <T> T errorAndThrowIfNull(@Nullable T object, String message) {
        assertNotNull(message, object);
        return object;
    }

    @Nullable
    @SuppressWarnings("ConstantOnRightSideOfComparison")
    public final void errorAndThrow(String message) {
        assertNotNull(message, null);
    }

    public void logMethodName() {
        Log.d(TAG, Thread.currentThread().getStackTrace()[3].getMethodName() + "() called");
    }

    public String getMethodName() {
        return getMethodName(1);
    }

    public String getMethodName(int methodDepthOffset) {
        return Thread.currentThread().getStackTrace()[3+methodDepthOffset].getMethodName();
    }

    public String getParentMethodName() {
        return getParentMethodName(1);
    }

    public String getParentMethodName(int methodDepthOffset) {
        return Thread.currentThread().getStackTrace()[4+methodDepthOffset].getMethodName();
    }
}
