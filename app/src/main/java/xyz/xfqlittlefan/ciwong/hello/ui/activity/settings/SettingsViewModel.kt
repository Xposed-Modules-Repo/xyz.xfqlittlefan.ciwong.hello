package xyz.xfqlittlefan.ciwong.hello.ui.activity.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import xyz.xfqlittlefan.ciwong.hello.R
import xyz.xfqlittlefan.ciwong.hello.ui.activity.settings.data.SettingsItem

class SettingsViewModel : ViewModel() {
    val itemList = mutableStateOf(
        listOf(
            SettingsItem(R.string.function_listen_speak_title, value = true)
        )
    )
}