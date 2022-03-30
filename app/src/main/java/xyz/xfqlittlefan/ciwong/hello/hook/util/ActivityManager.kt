package xyz.xfqlittlefan.ciwong.hello.hook.util

import xyz.xfqlittlefan.ciwong.hello.ui.settings.SettingsActivity

object ActivityManager {
    const val MODULE_ACTIVITY_INTENT = "hello_ciwong_xposed_module_activity"
    val moduleActivityList = listOf<String>(SettingsActivity::class.java.name)
}