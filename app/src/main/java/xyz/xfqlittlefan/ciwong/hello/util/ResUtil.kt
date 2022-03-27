package xyz.xfqlittlefan.ciwong.hello.util

import android.annotation.SuppressLint
import de.robv.android.xposed.callbacks.XC_InitPackageResources

object ResUtil {
    @SuppressLint("DiscouragedApi")
    fun XC_InitPackageResources.InitPackageResourcesParam.getId(type: String, name: String) =
        this.res.getIdentifier(name, type, "com.ciwong.newspaper")
}