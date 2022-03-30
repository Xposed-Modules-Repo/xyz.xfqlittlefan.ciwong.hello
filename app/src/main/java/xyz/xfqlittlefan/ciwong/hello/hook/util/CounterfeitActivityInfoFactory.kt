package xyz.xfqlittlefan.ciwong.hello.hook.util

import android.app.Application
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build

object CounterfeitActivityInfoFactory {
    fun makeProxyActivityInfo(
        className: String,
        application: Application,
        flags: Long
    ): ActivityInfo? {
        return try {
            try {
                val proto: ActivityInfo = application.packageManager.getActivityInfo(
                    ComponentName(
                        application.packageName, "com.ciwong.epaper.modules.me.ui.SettingActivity"
                    ), flags.toInt()
                )
                initCommon(proto, className)
            } catch (e: PackageManager.NameNotFoundException) {
                throw IllegalStateException(
                    "SettingActivity not found, are we in the host?", e
                )
            }
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    private fun initCommon(activityInfo: ActivityInfo, name: String): ActivityInfo {
        activityInfo.targetActivity = null
        activityInfo.taskAffinity = null
        activityInfo.descriptionRes = 0
        activityInfo.name = name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activityInfo.splitName = null
        }
        return activityInfo
    }
}
