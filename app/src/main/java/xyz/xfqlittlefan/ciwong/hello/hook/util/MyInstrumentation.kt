package xyz.xfqlittlefan.ciwong.hello.hook.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.app.UiAutomation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.*
import android.view.KeyEvent
import android.view.MotionEvent
import xyz.xfqlittlefan.ciwong.hello.hook.MainHooker

@Suppress("DEPRECATION")
@SuppressLint("NewApi")
class MyInstrumentation(private val mBase: Instrumentation) : Instrumentation() {
    override fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity {
        try {
            return mBase.newActivity(cl, className, intent)
        } catch (e: Throwable) {
            if (className != null && ActivityManager.isModuleActivity(className)) {
                return MainHooker::class.java.classLoader!!.loadClass(className)
                    .newInstance() as Activity
            }
            throw e
        }
    }

    override fun callActivityOnCreate(activity: Activity?, icicle: Bundle?) {
        if (activity != null && icicle != null) {
            val className = activity::class.java.name
            if (ActivityManager.isModuleActivity(className)) {
                icicle.classLoader = MainHooker::class.java.classLoader
            }
        }
        //Inject Resources Here
        mBase.callActivityOnCreate(activity, icicle)
    }

    override fun callActivityOnCreate(
        activity: Activity?, icicle: Bundle?, persistentState: PersistableBundle?
    ) {
        if (activity != null && icicle != null) {
            val className = activity::class.java.name
            if (ActivityManager.isModuleActivity(className)) {
                icicle.classLoader = MainHooker::class.java.classLoader
            }
        }
        //Inject Resources Here
        mBase.callActivityOnCreate(activity, icicle, persistentState)
    }

    override fun onCreate(arguments: Bundle?) = mBase.onCreate(arguments)


    override fun start() = mBase.start()


    override fun onStart() = mBase.onStart()


    override fun onException(obj: Any?, e: Throwable?): Boolean = mBase.onException(obj, e)


    override fun sendStatus(resultCode: Int, results: Bundle?) =
        mBase.sendStatus(resultCode, results)

    override fun addResults(results: Bundle?) = mBase.addResults(results)


    override fun finish(resultCode: Int, results: Bundle?) = mBase.finish(resultCode, results)


    override fun setAutomaticPerformanceSnapshots() = mBase.setAutomaticPerformanceSnapshots()


    override fun startPerformanceSnapshot() = mBase.startPerformanceSnapshot()


    override fun endPerformanceSnapshot() = mBase.endPerformanceSnapshot()


    override fun onDestroy() = mBase.onDestroy()


    override fun getContext(): Context = mBase.context

    override fun getComponentName(): ComponentName = mBase.componentName

    override fun getTargetContext(): Context = mBase.targetContext

    override fun getProcessName(): String = mBase.processName

    override fun isProfiling(): Boolean = mBase.isProfiling

    override fun startProfiling() = mBase.startProfiling()


    override fun stopProfiling() = mBase.stopProfiling()


    override fun setInTouchMode(inTouch: Boolean) = mBase.setInTouchMode(inTouch)


    override fun waitForIdle(recipient: Runnable?) = mBase.waitForIdle(recipient)


    override fun waitForIdleSync() = mBase.waitForIdleSync()


    override fun runOnMainSync(runner: Runnable?) = mBase.runOnMainSync(runner)


    override fun startActivitySync(intent: Intent?): Activity = mBase.startActivitySync(intent)

    override fun startActivitySync(intent: Intent, options: Bundle?) =
        mBase.startActivitySync(intent, options)

    override fun addMonitor(monitor: ActivityMonitor?) = mBase.addMonitor(monitor)

    override fun addMonitor(
        filter: IntentFilter?, result: ActivityResult?, block: Boolean
    ): ActivityMonitor = mBase.addMonitor(filter, result, block)

    override fun addMonitor(
        cls: String?, result: ActivityResult?, block: Boolean
    ): ActivityMonitor = mBase.addMonitor(cls, result, block)

    override fun checkMonitorHit(monitor: ActivityMonitor?, minHits: Int): Boolean =
        mBase.checkMonitorHit(monitor, minHits)

    override fun waitForMonitor(monitor: ActivityMonitor?): Activity = mBase.waitForMonitor(monitor)

    override fun waitForMonitorWithTimeout(monitor: ActivityMonitor?, timeOut: Long): Activity =
        mBase.waitForMonitorWithTimeout(monitor, timeOut)

    override fun removeMonitor(monitor: ActivityMonitor?) = mBase.removeMonitor(monitor)


    override fun invokeMenuActionSync(targetActivity: Activity?, id: Int, flag: Int): Boolean =
        mBase.invokeMenuActionSync(targetActivity, id, flag)

    override fun invokeContextMenuAction(targetActivity: Activity?, id: Int, flag: Int): Boolean =
        mBase.invokeContextMenuAction(targetActivity, id, flag)

    override fun sendStringSync(text: String?) = mBase.sendStringSync(text)


    override fun sendKeySync(event: KeyEvent?) = mBase.sendKeySync(event)


    override fun sendKeyDownUpSync(key: Int) = mBase.sendKeyDownUpSync(key)


    override fun sendCharacterSync(keyCode: Int) = mBase.sendCharacterSync(keyCode)


    override fun sendPointerSync(event: MotionEvent?) = mBase.sendPointerSync(event)


    override fun sendTrackballEventSync(event: MotionEvent?) = mBase.sendTrackballEventSync(event)


    override fun newApplication(
        cl: ClassLoader?, className: String?, context: Context?
    ): Application = mBase.newApplication(cl, className, context)

    override fun callApplicationOnCreate(app: Application?) = mBase.callApplicationOnCreate(app)


    override fun newActivity(
        clazz: Class<*>?,
        context: Context?,
        token: IBinder?,
        application: Application?,
        intent: Intent?,
        info: ActivityInfo?,
        title: CharSequence?,
        parent: Activity?,
        id: String?,
        lastNonConfigurationInstance: Any?
    ): Activity = mBase.newActivity(
        clazz,
        context,
        token,
        application,
        intent,
        info,
        title,
        parent,
        id,
        lastNonConfigurationInstance
    )

    override fun callActivityOnDestroy(activity: Activity?) = mBase.callActivityOnDestroy(activity)


    override fun callActivityOnRestoreInstanceState(
        activity: Activity, savedInstanceState: Bundle
    ) = mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState)


    override fun callActivityOnRestoreInstanceState(
        activity: Activity, savedInstanceState: Bundle?, persistentState: PersistableBundle?
    ) = mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState)


    override fun callActivityOnPostCreate(activity: Activity, savedInstanceState: Bundle?) =
        mBase.callActivityOnPostCreate(activity, savedInstanceState)


    override fun callActivityOnPostCreate(
        activity: Activity, savedInstanceState: Bundle?, persistentState: PersistableBundle?
    ) = mBase.callActivityOnPostCreate(activity, savedInstanceState, persistentState)


    override fun callActivityOnNewIntent(activity: Activity?, intent: Intent?) =
        mBase.callActivityOnNewIntent(activity, intent)


    override fun callActivityOnStart(activity: Activity?) = mBase.callActivityOnStart(activity)


    override fun callActivityOnRestart(activity: Activity?) = mBase.callActivityOnRestart(activity)


    override fun callActivityOnResume(activity: Activity?) = mBase.callActivityOnResume(activity)


    override fun callActivityOnStop(activity: Activity?) = mBase.callActivityOnStop(activity)


    override fun callActivityOnSaveInstanceState(activity: Activity, outState: Bundle) =
        mBase.callActivityOnSaveInstanceState(activity, outState)


    override fun callActivityOnSaveInstanceState(
        activity: Activity, outState: Bundle, outPersistentState: PersistableBundle
    ) = mBase.callActivityOnSaveInstanceState(activity, outState, outPersistentState)

    override fun callActivityOnPause(activity: Activity?) = mBase.callActivityOnPause(activity)

    override fun callActivityOnUserLeaving(activity: Activity?) =
        mBase.callActivityOnUserLeaving(activity)

    override fun callActivityOnPictureInPictureRequested(activity: Activity) =
        mBase.callActivityOnPictureInPictureRequested(activity)

    @Deprecated("Deprecated in Java")
    override fun startAllocCounting() = mBase.startAllocCounting()

    @Deprecated("Deprecated in Java")
    override fun stopAllocCounting() = mBase.stopAllocCounting()

    override fun getAllocCounts(): Bundle = mBase.allocCounts

    override fun getBinderCounts(): Bundle = mBase.binderCounts

    override fun getUiAutomation(): UiAutomation = mBase.uiAutomation

    override fun getUiAutomation(flags: Int): UiAutomation = mBase.getUiAutomation(flags)

    override fun acquireLooperManager(looper: Looper?): TestLooperManager =
        mBase.acquireLooperManager(looper)
}