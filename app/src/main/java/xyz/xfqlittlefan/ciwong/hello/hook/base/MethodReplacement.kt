package xyz.xfqlittlefan.ciwong.hello.hook.base

import de.robv.android.xposed.XC_MethodReplacement

abstract class MethodReplacement : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam?): Any? {
        if (param == null) return null

        return replacedMethod(param)
    }

    abstract fun replacedMethod(param: MethodHookParam?): Any
}