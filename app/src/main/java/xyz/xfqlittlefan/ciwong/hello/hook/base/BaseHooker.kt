package xyz.xfqlittlefan.ciwong.hello.hook.base

import android.app.Application
import android.content.res.XResources
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated
import de.robv.android.xposed.callbacks.XC_LoadPackage
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil

open class BaseHooker : IXposedHookLoadPackage, IXposedHookInitPackageResources {
    protected lateinit var classLoader: ClassLoader
    protected lateinit var application: Application

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam?.packageName != "com.ciwong.newspaper") return
        onAppLoaded()

        XposedBridge.hookAllMethods(XposedHelpers.findClass(
            "com.stub.StubApp", lpparam.classLoader
        ), "attachBaseContext", object : XC_MethodHook() {
            override fun afterHookedMethod(applicationParam: MethodHookParam?) {
                if (applicationParam == null) return

                application = applicationParam.args[0] as Application
                val classLoader = application.classLoader

                this@BaseHooker.classLoader = classLoader
                onAppFullyLoaded()
            }
        })
    }

    open fun onAppLoaded() {}

    open fun onAppFullyLoaded() {}

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        if (resparam?.packageName != "com.ciwong.newspaper") return
        LogUtil.log("Resources hooked!")

        onResourcesLoaded(resparam)
    }

    open fun onResourcesLoaded(resparam: XC_InitPackageResources.InitPackageResourcesParam) {}

    fun String.findAndHookMethod(
        methodName: String,
        doBefore: (param: XC_MethodHook.MethodHookParam) -> Unit = {},
        doAfter: (param: XC_MethodHook.MethodHookParam) -> Unit = {},
        vararg parameterTypes: Class<*>
    ): XC_MethodHook.Unhook? {
        return try {
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass(this, classLoader),
                methodName,
                *parameterTypes,
                object : MethodHook() {
                    override fun doBeforeHookedMethod(param: MethodHookParam) = doBefore(param)
                    override fun doAfterHookedMethod(param: MethodHookParam) = doAfter(param)
                })
        } catch (e: Throwable) {
            LogUtil.error(e)
            null
        }
    }

    fun XResources.hookLayout(
        type: String,
        name: String,
        onLayoutInflated: (liparam: XC_LayoutInflated.LayoutInflatedParam) -> Unit
    ): XC_LayoutInflated.Unhook {
        return hookLayout("com.ciwong.newspaper", type, name, object : LayoutInflated() {
            override fun onLayoutInflated(liparam: LayoutInflatedParam) = onLayoutInflated(liparam)
        })
    }
}