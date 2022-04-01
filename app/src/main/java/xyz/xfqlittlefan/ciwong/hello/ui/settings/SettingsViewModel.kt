package xyz.xfqlittlefan.ciwong.hello.ui.settings

import androidx.lifecycle.ViewModel
import xyz.xfqlittlefan.ciwong.hello.data.SettingsItem
import xyz.xfqlittlefan.ciwong.hello.hook.util.AppPreferences

class SettingsViewModel : ViewModel() {
    val itemList = listOf(
        SettingsItem(AppPreferences.NO_EXAM, "所有作业视为普通练习"),
        SettingsItem(AppPreferences.SHOW_ANSWERS_LISTEN_SPEAK, "听说模考显示答案"),
        SettingsItem(AppPreferences.SHOW_ANSWERS_ONLINE_PAPER, "在线作答显示答案")
    )
}