package xyz.xfqlittlefan.ciwong.hello.hook.util

import android.app.Application
import android.content.ComponentName
import android.content.pm.ActivityInfo
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class PackageManagerInvocationHandler(
    private val mTarget: Any, private val mApplication: Application
) : InvocationHandler {
    override operator fun invoke(proxy: Any?, method: Method, args: Array<Any>): Any? {
        return try {
            if ("getActivityInfo" == method.name) {
                LogUtil.log("PackageManagerInvocationHandler... 1")
                val activityInfo = method.invoke(mTarget, *args) as ActivityInfo?
                if (activityInfo != null) return activityInfo

                LogUtil.log("PackageManagerInvocationHandler... 2")
                val component = args[0] as ComponentName
                val flags = (args[1] as Number).toLong()
                if (component.packageName == "com.ciwong.newspaper" && ActivityManager.moduleActivityList.contains(
                        component.className
                    )
                ) {
                    LogUtil.log("PackageManagerInvocationHandler... 3")
                    CounterfeitActivityInfoFactory.makeProxyActivityInfo(
                        component.className, mApplication, flags
                    )

                    LogUtil.log("PackageManagerInvocationHandler... OK")
                } else {
                    null
                }
            } else {
                method.invoke(mTarget, *args)
            }
        } catch (ite: InvocationTargetException) {
            throw ite.targetException
        }
    }
}