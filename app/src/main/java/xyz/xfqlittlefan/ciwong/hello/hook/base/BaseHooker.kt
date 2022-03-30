package xyz.xfqlittlefan.ciwong.hello.hook.base

import android.content.Context
import android.content.res.XResources
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated
import de.robv.android.xposed.callbacks.XC_LoadPackage
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil

open class BaseHooker : IXposedHookLoadPackage, IXposedHookInitPackageResources {
    private lateinit var classLoader: ClassLoader

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam?.packageName != "com.ciwong.newspaper") return
        LogUtil.log("Hello, Ciwong!")

        XposedBridge.hookAllMethods(XposedHelpers.findClass(
            "com.stub.StubApp", lpparam.classLoader
        ), "attachBaseContext", object : XC_MethodHook() {
            override fun afterHookedMethod(contextParam: MethodHookParam?) {
                if (contextParam == null) return
                LogUtil.log("Qihoo, I heard that you want to protect Ciwong?")

                val context = contextParam.args[0] as Context
                val classLoader = context.classLoader ?: return

                this@BaseHooker.classLoader = classLoader
                onPackageLoaded()
            }
        })
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        if (resparam?.packageName != "com.ciwong.newspaper") return
        LogUtil.log("Resources hooked!")

        onResourcesLoaded(resparam)
    }

    open fun onResourcesLoaded(resparam: XC_InitPackageResources.InitPackageResourcesParam) {}

    open fun onPackageLoaded() {}

    fun findAndHookMethod(
        className: String?,
        methodName: String?,
        vararg parameterTypesAndCallback: Any?
    ): XC_MethodHook.Unhook? {
        return try {
            XposedHelpers.findAndHookMethod(
                XposedHelpers.findClass(className, classLoader),
                methodName,
                *parameterTypesAndCallback
            )
        } catch (e: Throwable) {
            LogUtil.error(e)
            null
        }
    }

    fun XResources.hookLayout(
        type: String, name: String, callback: XC_LayoutInflated?
    ): XC_LayoutInflated.Unhook {
        return hookLayout("com.ciwong.newspaper", type, name, callback)
    }
}