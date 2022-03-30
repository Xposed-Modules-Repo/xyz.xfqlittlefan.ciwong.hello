package xyz.xfqlittlefan.ciwong.hello.hook.util

import android.annotation.SuppressLint
import android.app.Application
import android.app.Instrumentation
import xyz.xfqlittlefan.ciwong.hello.hook.MainHooker
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil
import java.lang.reflect.Field
import java.lang.reflect.Proxy


object Injector {
    var activityOk = false

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    fun prepareActivity(application: Application) {
        if (activityOk) return
        try {
            val classActivityThread = Class.forName("android.app.ActivityThread")
            val methodCurrentActivityThread =
                classActivityThread.getDeclaredMethod("currentActivityThread")
            methodCurrentActivityThread.isAccessible = true
            val currentActivityThread = methodCurrentActivityThread.invoke(null)
            val fieldMInstrumentation = classActivityThread.getDeclaredField("mInstrumentation")
            fieldMInstrumentation.isAccessible = true
            val instrumentation =
                fieldMInstrumentation.get(currentActivityThread) as Instrumentation
            fieldMInstrumentation.set(currentActivityThread, MyInstrumentation(instrumentation))

            val fieldMH = classActivityThread.getDeclaredField("mH")
            fieldMH.isAccessible = true
            val mHHandler = fieldMH.get(currentActivityThread)
            val fieldMCallback = android.os.Handler::class.java.getDeclaredField("mCallback")
            fieldMCallback.isAccessible = true
            val callback = fieldMCallback.get(mHHandler) as android.os.Handler.Callback?
            if (callback == null || callback::class.java.name != MyH::class.java.name) {
                fieldMCallback.set(mHHandler, MyH(callback, application.classLoader))
            }

            var classActivityManager: Class<*>
            var fieldGDefault: Field
            try {
                classActivityManager = Class.forName("android.app.ActivityManagerNative")
                fieldGDefault = classActivityManager.getDeclaredField("gDefault")
            } catch (e1: Throwable) {
                try {
                    classActivityManager = Class.forName("android.app.ActivityManager")
                    fieldGDefault =
                        classActivityManager.getDeclaredField("IActivityManagerSingleton")
                } catch (e2: Throwable) {
                    LogUtil.error(e1)
                    LogUtil.error(e2)
                    return
                }
            }
            fieldGDefault.isAccessible = true
            val gDefault = fieldGDefault.get(null)
            val classSingleton = Class.forName("android.util.Singleton")
            val fieldMInstance = classSingleton.getDeclaredField("mInstance")
            fieldMInstance.isAccessible = true
            val mInstance = fieldMInstance.get(gDefault)
            val activityManagerProxy = Proxy.newProxyInstance(
                MainHooker::class.java.classLoader,
                arrayOf(Class.forName("android.app.IActivityTaskManager")),
                ActivityManagerHandler(mInstance!!, application)
            )
            fieldMInstance.set(gDefault, activityManagerProxy)

            val fieldSPackageManager = classActivityThread.getDeclaredField("sPackageManager")
            fieldSPackageManager.isAccessible = true
            val packageManagerImpl = fieldSPackageManager.get(currentActivityThread)
            val classIPackageManager = Class.forName("android.content.pm.IPackageManager")
            val packageManager = application.packageManager
            val fieldMPM = packageManager.javaClass.getDeclaredField("mPM")
            fieldMPM.isAccessible = true
            val packageManagerProxy = Proxy.newProxyInstance(
                classIPackageManager.classLoader,
                arrayOf(classIPackageManager),
                PackageManagerInvocationHandler(packageManagerImpl!!, application)
            )
            fieldSPackageManager.set(currentActivityThread, packageManagerProxy)
            fieldMPM.set(packageManager, packageManagerProxy)

            activityOk = true
        } catch (e: Throwable) {
            LogUtil.error(e)
        }
    }
}