package xyz.xfqlittlefan.ciwong.hello.hook.util

import android.app.Application
import android.content.Intent
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class ActivityManagerHandler(private val mOrigin: Any, private val mApplication: Application) :
    InvocationHandler {
    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        if (args != null && method.name == "startActivity") {
            var index: Int? = null
            for (argsIndex in args.indices) {
                val arg = args[argsIndex]
                if (arg is Intent) {
                    index = argsIndex
                    break
                }
            }
            val intent = index?.let { args[it] as Intent }
            val component = intent?.component
            if (component != null && mApplication.packageName == component.packageName && ActivityManager.moduleActivityList.contains(
                    component.className
                )
            ) {
                args[index!!] = Intent().apply {
                    setClassName(
                        component.packageName, "com.ciwong.epaper.modules.me.ui.SettingActivity"
                    )
                    putExtra(ActivityManager.MODULE_ACTIVITY_INTENT, intent)
                }
            }
        }
        try {
            return if (args != null) method.invoke(mOrigin, *args) else method.invoke(mOrigin)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }
}
