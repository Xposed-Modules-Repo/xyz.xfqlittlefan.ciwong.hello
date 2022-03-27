package xyz.xfqlittlefan.ciwong.hello.hook

import android.content.Context
import androidx.annotation.Keep
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

@Keep
class MainHooker : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam?.packageName != "com.ciwong.newspaper") return
        XposedBridge.log("[HelloCiwong] Hello, Ciwong!")

        XposedBridge.hookAllMethods(XposedHelpers.findClass(
            "com.stub.StubApp", lpparam.classLoader
        ), "attachBaseContext", object : XC_MethodHook() {
            override fun afterHookedMethod(contextParam: MethodHookParam?) {
                if (contextParam == null) return
                XposedBridge.log("[HelloCiwong] Qihoo, I heard that you want to protect Ciwong?")

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
                            XposedBridge.log(
                                "[HelloCiwong] Oh, com.ciwong.epaper.modules.epaper.a.b$4#success hooked! b's name is: ${
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
                            XposedBridge.log("[HelloCiwong] Listen speak exam process finished! Result: $result")
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
                            XposedBridge.log("[HelloCiwong] Ciwong, show the answer!")

                            val thisClassLoader = this@MainHooker::class.java.classLoader
                                ?: throw Exception("[HelloCiwong] HelloCiwong has got a strange problem 0x1.")

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
}