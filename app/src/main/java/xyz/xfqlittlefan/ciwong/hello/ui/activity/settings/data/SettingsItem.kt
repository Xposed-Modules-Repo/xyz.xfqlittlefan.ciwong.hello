package xyz.xfqlittlefan.ciwong.hello.ui.activity.settings.data

import androidx.annotation.StringRes

data class SettingsItem(
    @StringRes val title: Int,
    @StringRes val description: Int? = null,
    val value: Any
)