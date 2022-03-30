package xyz.xfqlittlefan.ciwong.hello.hook.base

import de.robv.android.xposed.XC_MethodHook

abstract class MethodHook : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam?) {
        if (param == null) return

        doBeforeHookedMethod(param)
    }

    open fun doBeforeHookedMethod(param: MethodHookParam) {}

    override fun afterHookedMethod(param: MethodHookParam?) {
        if (param == null) return

        doAfterHookedMethod(param)
    }

    open fun doAfterHookedMethod(param: MethodHookParam) {}
}