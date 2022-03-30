package xyz.xfqlittlefan.ciwong.hello.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Keep
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.setPadding
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import xyz.xfqlittlefan.ciwong.hello.hook.base.BaseHooker
import xyz.xfqlittlefan.ciwong.hello.hook.base.LayoutInflated
import xyz.xfqlittlefan.ciwong.hello.hook.base.MethodHook
import xyz.xfqlittlefan.ciwong.hello.ui.settings.Settings
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil
import xyz.xfqlittlefan.ciwong.hello.util.ResUtil.getId
import kotlin.math.roundToInt

@Keep
class SettingsHooker : BaseHooker() {
    override fun onPackageLoaded() {
        findAndHookMethod("com.ciwong.epaper.modules.me.ui.SettingActivity",
            "initEvent",
            object : MethodHook() {
                @SuppressLint("ResourceType")
                override fun doBeforeHookedMethod(param: MethodHookParam) {
                    LogUtil.log("We are in SettingActivity!")
                    val activity = param.thisObject as Activity
                    val item = activity.findViewById<RelativeLayout>(-0x2)
                    item.setOnClickListener {
                        Dialog(activity).apply {
                            setContentView(ComposeView(activity).apply {
                                setContent {
                                    Settings(onClose = { cancel() })
                                }
                            })
                            show()
                        }
                    }
                }
            })
    }

    override fun onResourcesLoaded(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        resparam.res.hookLayout("layout", "activity_setting", object : LayoutInflated() {
            @SuppressLint("SetTextI18n", "ResourceType")
            override fun onLayoutInflated(liparam: LayoutInflatedParam) {
                val root =
                    ((liparam.view as FrameLayout).getChildAt(1) as ScrollView).getChildAt(0) as LinearLayout

                val context = root.context
                val moduleSettingsItem = RelativeLayout(context)

                val layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )

                moduleSettingsItem.layoutParams = layoutParams
                moduleSettingsItem.background = resparam.res.getDrawable(
                    resparam.getId("drawable", "list_item_bg_selector"), null
                )
                moduleSettingsItem.setPadding(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 20f, context.resources.displayMetrics
                    ).roundToInt()
                )
                moduleSettingsItem.id = -0x2

                val moduleSettingsItemText = TextView(context)

                moduleSettingsItemText.layoutParams = layoutParams
                moduleSettingsItemText.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX, resparam.res.getDimension(
                        resparam.getId("dimen", "text_size_medium")
                    )
                )
                moduleSettingsItemText.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, resparam.res.getDrawable(
                        resparam.getId("mipmap", "public_btn_enter"), null
                    ), null
                )
                moduleSettingsItemText.text = "HelloCiwong 设置"

                moduleSettingsItem.addView(moduleSettingsItemText)

                root.addView(moduleSettingsItem, 0)

                liparam.view = root
            }
        })
    }
}