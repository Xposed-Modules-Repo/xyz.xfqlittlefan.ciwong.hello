package xyz.xfqlittlefan.ciwong.hello.ui.base

import android.view.Window
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat

open class BaseActivity : ComponentActivity() {
    fun Window.setCompatDecorFitsSystemWindows(value: Boolean) =
        WindowCompat.setDecorFitsSystemWindows(window, value)
}