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
                LogUtil.log("MyH, 100... 1")
                val obj = msg.obj
                val fieldIntent = obj.javaClass.getDeclaredField("intent")
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

                LogUtil.log("MyH, 100... 2")
                bundle.classLoader = mHostClassLoader
                if (!intent.hasExtra(ActivityManager.MODULE_ACTIVITY_INTENT)) return false

                LogUtil.log("MyH, 100... 3")
                val realIntent =
                    intent.getParcelableExtra<Intent>(ActivityManager.MODULE_ACTIVITY_INTENT)
                fieldIntent.set(obj, realIntent)
                LogUtil.log("MyH, 100... OK")
            } catch (e: Throwable) {
                LogUtil.error(e)
            }
        } else if (msg.what == 159) { //EXECUTE_TRANSACTION
            try {
                LogUtil.log("MyH, 159... 1")
                val clientTransaction = msg.obj ?: return false

                LogUtil.log("MyH, 159... 2")
                val methodGetCallbacks =
                    Class.forName("android.app.servertransaction.ClientTransaction")
                        .getDeclaredMethod("getCallbacks")
                methodGetCallbacks.isAccessible = true
                val clientTransactionItemList =
                    methodGetCallbacks.invoke(clientTransaction) as List<*>
                if (clientTransactionItemList.isEmpty()) return false

                LogUtil.log("MyH, 159... 3")
                for (item in clientTransactionItemList) {
                    LogUtil.log("MyH, 159... 3... 1")
                    if (item == null) continue

                    LogUtil.log("MyH, 159... 3... 2")
                    val itemClass = item.javaClass
                    LogUtil.log("itemClass.name: ${itemClass.name}")
                    if (!itemClass.name.contains("LaunchActivityItem")) continue

                    LogUtil.log("MyH, 159... 3... 3")
                    val fieldMIntent = itemClass.getDeclaredField("mIntent")
                    fieldMIntent.isAccessible = true
                    val intent = fieldMIntent.get(item) as Intent
                    LogUtil.log("intent: ${intent}")
                    var bundle: Bundle? = null
                    try {
                        val fieldMExtras = Intent::class.java.getDeclaredField("mExtras")
                        fieldMExtras.isAccessible = true
                        LogUtil.log("fieldMExtras.get(intent): ${fieldMExtras.get(intent)}")
                        bundle = fieldMExtras.get(intent) as Bundle
                    } catch (e: Throwable) {
                        LogUtil.error(e)
                    }
                    if (bundle == null) continue

                    LogUtil.log("MyH, 159... 3... 4")
                    bundle.classLoader = mHostClassLoader
                    if (!intent.hasExtra(ActivityManager.MODULE_ACTIVITY_INTENT)) continue

                    LogUtil.log("MyH, 159... 3... 5")
                    val realIntent =
                        intent.getParcelableExtra<Intent>(ActivityManager.MODULE_ACTIVITY_INTENT)
                    fieldMIntent.set(item, realIntent)
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) continue

                    LogUtil.log("MyH, 159... 3... 6")
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

                    LogUtil.log("MyH, 159... 3... 7")
                    val fieldIntent = activity.javaClass.getDeclaredField("intent")
                    fieldIntent.isAccessible = true
                    fieldIntent.set(activity, realIntent)
                    LogUtil.log("MyH, 159... 3... OK")
                }
                LogUtil.log("MyH, 159... OK")
            } catch (e: Throwable) {
                LogUtil.error(e)
            }
        }
        if (mDefault != null) {
            val result = mDefault.handleMessage(msg)
            LogUtil.log("MyH, OK")
            return result
        }
        LogUtil.log("MyH, false")
        return false
    }
}