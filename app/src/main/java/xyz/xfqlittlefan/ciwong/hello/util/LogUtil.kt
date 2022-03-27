package xyz.xfqlittlefan.ciwong.hello.util

import de.robv.android.xposed.XposedBridge

object LogUtil {
    fun log(message: String) {
        XposedBridge.log("[HelloCiwong] $message")
    }
}