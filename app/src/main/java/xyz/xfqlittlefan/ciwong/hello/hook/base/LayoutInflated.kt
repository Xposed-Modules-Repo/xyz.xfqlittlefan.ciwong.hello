package xyz.xfqlittlefan.ciwong.hello.hook.base

import de.robv.android.xposed.callbacks.XC_LayoutInflated

abstract class LayoutInflated : XC_LayoutInflated() {
    override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
        if (liparam == null) return

        onLayoutInflated(liparam)
    }

    abstract fun onLayoutInflated(liparam: LayoutInflatedParam)
}