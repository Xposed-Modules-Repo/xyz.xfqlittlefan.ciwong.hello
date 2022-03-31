package xyz.xfqlittlefan.ciwong.hello.ui.settings

import androidx.lifecycle.ViewModel
import xyz.xfqlittlefan.ciwong.hello.data.SettingsItem

class SettingsViewModel : ViewModel() {
    val itemList = listOf(
        SettingsItem("no_exam", "所有作业视为普通练习"),
        SettingsItem("show_answers_listen_speak", "听说模考显示答案"),
        SettingsItem("show_answers_online_paper", "在线作答显示答案")
    )
}