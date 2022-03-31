package xyz.xfqlittlefan.ciwong.hello.hook.util

import android.app.Application
import android.content.Intent
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class ActivityManagerHandler(private val mOrigin: Any, private val mApplication: Application) :
    InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        if (args != null && method.name == "startActivity") {
            LogUtil.log("ActivityManagerHandler... 1")
            var index: Int? = null
            for (argsIndex in args.indices) {
                val arg = args[argsIndex]
                if (arg is Intent) {
                    index = argsIndex
                    break
                }
            }
            LogUtil.log("ActivityManagerHandler... 2")
            val intent = index?.let { args[it] as Intent }
            val component = intent?.component
            if (component != null && mApplication.packageName == component.packageName && ActivityManager.moduleActivityList.contains(
                    component.className
                )
            ) {
                LogUtil.log("ActivityManagerHandler... 3")
                args[index!!] = Intent().apply {
                    setClassName(
                        component.packageName, "com.ciwong.epaper.modules.me.ui.SettingActivity"
                    )
                    putExtra(ActivityManager.MODULE_ACTIVITY_INTENT, intent)
                }
                LogUtil.log("ActivityManagerHandler... 4")
            }
        }
        try {
            val result = if (args != null) method.invoke(mOrigin, *args) else method.invoke(mOrigin)
            LogUtil.log("ActivityManagerHandler... OK")
            return result
        } catch (e: InvocationTargetException) {
            LogUtil.log("ActivityManagerHandler... Error")
            throw e.targetException
        }
    }
}
