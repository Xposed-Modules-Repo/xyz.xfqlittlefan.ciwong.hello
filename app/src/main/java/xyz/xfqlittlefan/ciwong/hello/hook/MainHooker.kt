package xyz.xfqlittlefan.ciwong.hello.hook

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Keep
import androidx.core.view.setPadding
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONObject
import xyz.xfqlittlefan.ciwong.hello.R
import xyz.xfqlittlefan.ciwong.hello.throwable.HelloCiwongImpossibleException
import xyz.xfqlittlefan.ciwong.hello.ui.activity.settings.SettingsActivity
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil
import xyz.xfqlittlefan.ciwong.hello.util.ResUtil.getId
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@Keep
class MainHooker : IXposedHookLoadPackage, IXposedHookInitPackageResources {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam?.packageName != "com.ciwong.newspaper") return
        LogUtil.log("Hello, Ciwong!")

        XposedBridge.hookAllMethods(XposedHelpers.findClass(
            "com.stub.StubApp", lpparam.classLoader
        ), "attachBaseContext", object : XC_MethodHook() {
            override fun afterHookedMethod(contextParam: MethodHookParam?) {
                if (contextParam == null) return
                LogUtil.log("Qihoo, I heard that you want to protect Ciwong?")

                val context = contextParam.args[0] as Context
                val classLoader = context.classLoader ?: return

                try {
                    XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                        "com.ciwong.epaper.modules.epaper.a.b$4", classLoader
                    ), "success", Any::class.java, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            if (param == null || (XposedHelpers.getObjectField(
                                    param.thisObject, "b"
                                ) as? Class<*>)?.name != "com.ciwong.epaper.modules.epaper.bean.ListenspeakExam"
                            ) return
                            LogUtil.log(
                                "Oh, com.ciwong.epaper.modules.epaper.a.b$4#success hooked! b's name is: ${
                                    (XposedHelpers.getObjectField(
                                        param.thisObject, "b"
                                    ) as? Class<*>)?.name
                                }"
                            )

                            val info = JSONObject(param.args[0] as String)

                            fun process(question: JSONObject) {
                                if (question.getJSONArray("children").length() == 0) {
                                    var answer = "\n\n\n"

                                    val optionArray = question.getJSONArray("options")
                                    for (optionIndex in 0 until optionArray.length()) {
                                        val option = optionArray.getJSONObject(optionIndex)

                                        if (option.getInt("isAnswer") == 1) {
                                            val valueArray = option.getJSONArray("value")
                                            for (valueIndex in 0 until valueArray.length()) {
                                                val value = valueArray.getJSONObject(valueIndex)

                                                answer += "${value.getString("body")}\n"
                                            }

                                            answer += "\n"
                                        }
                                    }

                                    question.getJSONObject("trunk").put(
                                        "body",
                                        question.getJSONObject("trunk").getString("body") + answer
                                    )
                                } else {
                                    val childArray = question.getJSONArray("children")
                                    for (childIndex in 0 until childArray.length()) {
                                        val child = childArray.getJSONObject(childIndex)

                                        process(child)
                                    }
                                }
                            }

                            val itemArray = info.getJSONArray("items")
                            for (itemIndex in 0 until itemArray.length()) {
                                val item = itemArray.getJSONObject(itemIndex)

                                val questionArray = item.getJSONArray("questions")
                                for (questionIndex in 0 until questionArray.length()) {
                                    process(questionArray.getJSONObject(questionIndex))
                                }
                            }

                            val result = info.toString()
                            LogUtil.log("Listen speak exam process finished! Result: $result")
                            param.args[0] = result
                        }
                    })
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

                try {
                    XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                        "com.ciwong.epaper.modules.me.bean.WorkContents", classLoader
                    ), "getExamMode", object : XC_MethodReplacement() {
                        override fun replaceHookedMethod(param: MethodHookParam?) = 0
                    })
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

                try {
                    XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
                        "com.ciwong.epaper.util.m", classLoader
                    ), "e", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam?) {
                            if (param == null) return
                            LogUtil.log("Ciwong, show the answer!")

                            val thisClassLoader = this@MainHooker::class.java.classLoader
                                ?: throw HelloCiwongImpossibleException("HelloCiwong has got a strange problem 0x1.")

                            thisClassLoader.getResourceAsStream("assets/paper.html")
                                .copyTo(FileOutputStream("${param.result as String}${File.separator}paper.html"))
                            thisClassLoader.getResourceAsStream("assets/online_question.js")
                                .copyTo(FileOutputStream("${param.result as String}${File.separator}common${File.separator}components${File.separator}online_question.js"))
                        }
                    })
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        if (resparam?.packageName != "com.ciwong.newspaper") return
        LogUtil.log("Resources hooked!")

        resparam.res.hookLayout("com.ciwong.newspaper",
            "layout",
            "activity_setting",
            object : XC_LayoutInflated() {
                @SuppressLint("SetTextI18n")
                override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
                    if (liparam == null) return

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
                    moduleSettingsItem.setOnClickListener {
                        it.context.startActivity(Intent(it.context, SettingsActivity::class.java))
                    }

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
                    moduleSettingsItemText.setText(R.string.settings)

                    moduleSettingsItem.addView(moduleSettingsItemText)

                    root.addView(moduleSettingsItem, 0)

                    liparam.view = root
                }
            })
    }
}