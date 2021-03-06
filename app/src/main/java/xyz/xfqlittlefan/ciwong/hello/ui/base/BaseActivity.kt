package xyz.xfqlittlefan.ciwong.hello.ui.base

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat

open class BaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setCompatDecorFitsSystemWindows(false)
    }

    private fun Window.setCompatDecorFitsSystemWindows(value: Boolean) =
        WindowCompat.setDecorFitsSystemWindows(window, value)
}