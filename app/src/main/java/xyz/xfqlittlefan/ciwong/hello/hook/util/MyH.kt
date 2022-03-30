package xyz.xfqlittlefan.ciwong.hello.hook.util

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil

class MyH(private val mDefault: Handler.Callback?, private val mHostClassLoader: ClassLoader) :
    Handler.Callback {
    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 100) { //LAUNCH_ACTIVITY
            try {
                val obj = msg.obj
                val fieldIntent = obj::class.java.getDeclaredField("intent")
                fieldIntent.isAccessible = true
                val intent = fieldIntent.get(obj) as Intent
                var bundle: Bundle? = null
                try {
                    val fieldMExtras = Intent::class.java.getDeclaredField("mExtras")
                    fieldMExtras.isAccessible = true
                    bundle = fieldMExtras.get(intent) as Bundle
                } catch (e: Throwable) {
                    LogUtil.error(e)
                }
                if (bundle == null) return false

                bundle.classLoader = mHostClassLoader
                if (!intent.hasExtra(ActivityManager.MODULE_ACTIVITY_INTENT)) return false

                val realIntent =
                    intent.getParcelableExtra<Intent>(ActivityManager.MODULE_ACTIVITY_INTENT)
                fieldIntent.set(obj, realIntent)
            } catch (e: Throwable) {
                LogUtil.error(e)
            }
        } else if (msg.what == 159) { //EXECUTE_TRANSACTION
            try {
                val clientTransaction = msg.obj ?: return false

                val methodGetCallbacks =
                    Class.forName("android.app.servertransaction.ClientTransaction")
                        .getDeclaredMethod("getCallbacks")
                methodGetCallbacks.isAccessible = true
                val clientTransactionItemList =
                    methodGetCallbacks.invoke(clientTransaction) as? List<*>
                if (clientTransactionItemList?.isNotEmpty() != true) return false

                for (item in clientTransactionItemList) {
                    if (item == null) continue
                    val itemClass = item.javaClass
                    if (!itemClass.name.contains("LaunchActivityItem")) continue

                    val fieldMIntent = itemClass.getDeclaredField("mIntent")
                    fieldMIntent.isAccessible = true
                    val intent = fieldMIntent.get(item) as Intent
                    var bundle: Bundle? = null
                    try {
                        val fieldMExtras = Intent::class.java.getDeclaredField("mExtras")
                        fieldMExtras.isAccessible = true
                        bundle = fieldMExtras.get(intent) as Bundle
                    } catch (e: Throwable) {
                        LogUtil.error(e)
                    }
                    if (bundle == null) continue

                    bundle.classLoader = mHostClassLoader
                    if (!intent.hasExtra(ActivityManager.MODULE_ACTIVITY_INTENT)) continue

                    val realIntent =
                        intent.getParcelableExtra<Intent>(ActivityManager.MODULE_ACTIVITY_INTENT)
                    fieldMIntent.set(item, realIntent)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) continue

                    val activityToken = clientTransaction.javaClass.getMethod("getActivityToken")
                        .invoke(clientTransaction) as IBinder
                    val classActivityThread = Class.forName("android.app.ActivityThread")
                    val methodCurrentActivityThread =
                        classActivityThread.getDeclaredMethod("currentActivityThread")
                    methodCurrentActivityThread.isAccessible = true
                    val currentActivityThread = methodCurrentActivityThread.invoke(null)
                    val activity = currentActivityThread.javaClass.getMethod(
                        "getLaunchingActivity", IBinder::class.java
                    ).invoke(currentActivityThread, activityToken) ?: continue

                    val fieldIntent = activity.javaClass.getDeclaredField("intent")
                    fieldIntent.isAccessible = true
                    fieldIntent.set(activity, realIntent)
                }
            } catch (e: Throwable) {
                LogUtil.error(e)
            }
        }
        if (mDefault != null) return mDefault.handleMessage(msg)
        return false
    }
}