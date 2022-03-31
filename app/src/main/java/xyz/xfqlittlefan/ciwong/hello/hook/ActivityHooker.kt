package xyz.xfqlittlefan.ciwong.hello.hook

import androidx.annotation.Keep
import xyz.xfqlittlefan.ciwong.hello.hook.base.BaseHooker

@Keep
class ActivityHooker : BaseHooker() {
    override fun onAppFullyLoaded() {
        "com.ciwong.mobilelib.ui.BaseActivity".hookAllMethods("checkSource", doBefore = {
            it.result = true
        })
    }
}