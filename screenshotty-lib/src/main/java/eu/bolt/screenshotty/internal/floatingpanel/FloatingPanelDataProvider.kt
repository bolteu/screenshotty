package eu.bolt.screenshotty.internal.floatingpanel

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.WindowManager
import eu.bolt.screenshotty.internal.Utils
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.HashMap

@Suppress("UNCHECKED_CAST")
internal class FloatingPanelDataProvider {

    private val fieldsCache = HashMap<FieldCacheKey, Field>()
    private var cachedGlobalWindowManager: Any? = null

    private val outLocation = IntArray(2)

    fun getFloatingPanels(activity: Activity): List<FloatingPanelData> = try {
        getFloatingPanelsInternal(activity)
    } catch (e: Exception) {
        Utils.logE(e)
        emptyList()
    }

    private fun getFloatingPanelsInternal(activity: Activity): List<FloatingPanelData> {
        val globalWindowManager = getGlobalWindowManager(activity)

        val viewRootsObject = getNotNullFieldValue("mRoots", globalWindowManager)
        val paramsObject = getNotNullFieldValue("mParams", globalWindowManager)

        val viewRootImpls = asViewRootsArray(viewRootsObject)
        val params = asWindowLayoutParamsArray(paramsObject)

        if (viewRootImpls == null || params == null) {
            logNullWindowData(viewRootImpls, params)
            return emptyList()
        }

        return collectData(activity, viewRootImpls, params)
    }

    private fun collectData(activity: Activity, roots: Array<Any>, params: Array<WindowManager.LayoutParams>): List<FloatingPanelData> {
        val rootViews = ArrayList<FloatingPanelData>()
        for (i in roots.indices) {
            if (isActivity(params[i])) {
                continue
            }
            val root = roots[i]
            val view = getNotNullFieldValue("mView", root) as? View
            if (view == null) {
                Utils.logE("null View or Window stored in Global window manager, skipping")
                continue
            }
            val activityContext = unwrapActivity(view.context)
            if (activityContext !== activity || !view.isShown) {
                continue
            }
            rootViews.add(FloatingPanelData(view, getViewRect(view), params[i]))
        }

        return rootViews
    }

    private fun asViewRootsArray(viewRootImpls: Any): Array<Any>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            (viewRootImpls as? List<Any>)?.toTypedArray()
        } else {
            viewRootImpls as? Array<Any>
        }
    }

    private fun asWindowLayoutParamsArray(params: Any): Array<WindowManager.LayoutParams>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            (params as? List<WindowManager.LayoutParams>)?.toTypedArray()
        } else {
            params as? Array<WindowManager.LayoutParams>
        }
    }

    private fun unwrapActivity(context: Context?): Context? {
        var result = context
        while (result != null) {
            if (result is Activity) {
                return result
            } else if (result is ContextWrapper) {
                result = result.baseContext
            } else {
                return null
            }
        }
        return null
    }

    private fun logNullWindowData(roots: Array<Any>?, params: Array<WindowManager.LayoutParams>?) {
        val e = IllegalStateException("failed to get view roots or params: $roots, $params")
        Utils.logE(e)
    }

    private fun getViewRect(view: View): Rect {
        view.getLocationOnScreen(outLocation)
        val left = outLocation[0]
        val top = outLocation[1]
        return Rect(left, top, left + view.width, top + view.height)
    }

    private fun getNotNullFieldValue(fieldName: String, target: Any): Any {
        val cacheKey = FieldCacheKey(target.javaClass, fieldName)
        val field = fieldsCache.getOrPut(cacheKey) {
            findField(target.javaClass, fieldName).apply {
                isAccessible = true
            }
        }
        return requireNotNull(field.get(target))
    }

    private fun findField(clazz: Class<*>, name: String): Field {
        var currentClass: Class<*>? = clazz
        while (currentClass != null) {
            val target = currentClass.declaredFields.find { it.name == name }
            if (target != null) {
                return target
            }
            currentClass = currentClass.superclass
        }
        throw NoSuchFieldException("Field $name not found for class $clazz")
    }

    //Cache the field, because it's context independent
    private fun getGlobalWindowManager(activity: Activity): Any {
        var wm = cachedGlobalWindowManager
        if (wm == null) {
            wm = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                getNotNullFieldValue("mWindowManager", activity.windowManager)
            } else {
                getNotNullFieldValue("mGlobal", activity.windowManager)
            }
            cachedGlobalWindowManager = wm
        }
        return wm
    }

    private fun isActivity(params: WindowManager.LayoutParams) = params.type == WindowManager.LayoutParams.TYPE_BASE_APPLICATION

    private data class FieldCacheKey(
        val clazz: Class<*>,
        val fieldName: String
    )

    companion object {
        private val INSTANCE = FloatingPanelDataProvider()

        @JvmStatic
        fun getInstance() = INSTANCE
    }
}
