package xyz.xfqlittlefan.ciwong.hello.hook

import androidx.annotation.Keep
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedHelpers
import org.json.JSONObject
import xyz.xfqlittlefan.ciwong.hello.hook.base.BaseHooker
import xyz.xfqlittlefan.ciwong.hello.hook.util.Injector
import xyz.xfqlittlefan.ciwong.hello.throwable.HelloCiwongImpossibleException
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil
import java.io.File
import java.io.FileOutputStream

@Keep
class MainHooker : BaseHooker() {
    override fun onAppLoaded() {
        LogUtil.log("Hello, Ciwong!")
    }

    override fun onAppFullyLoaded() {
        LogUtil.log("Qihoo, I heard that you want to protect Ciwong?")

        Injector.prepareActivity(application)

        val xSharedPreferences = XSharedPreferences("xyz.xfqlittlefan.ciwong.hello")

        if (xSharedPreferences.getBoolean("settings_no_exam", true)) {
            "com.ciwong.epaper.modules.me.bean.WorkContents".findAndHookMethod("getExamMode",
                doBefore = {
                    it.result = 0
                })
        }

        if (xSharedPreferences.getBoolean("settings_show_answers_listen_speak", true)) {
            "com.ciwong.epaper.modules.epaper.a.b$4".findAndHookMethod(
                "success", doBefore = { param ->
                    if ((XposedHelpers.getObjectField(
                            param.thisObject, "b"
                        ) as? Class<*>)?.name != "com.ciwong.epaper.modules.epaper.bean.ListenspeakExam"
                    ) return@findAndHookMethod
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
                                "body", question.getJSONObject("trunk").getString("body") + answer
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
                }, parameterTypes = arrayOf(Any::class.java)
            )
        }

        if (xSharedPreferences.getBoolean("settings_show_answers_online_paper", true)) {
            "com.ciwong.epaper.util.m".findAndHookMethod("e", doAfter = {
                LogUtil.log("Ciwong, show the answer!")

                val thisClassLoader =
                    this@MainHooker::class.java.classLoader ?: throw HelloCiwongImpossibleException(
                        "HelloCiwong has got a strange problem 0x1."
                    )

                thisClassLoader.getResourceAsStream("assets/paper.html")
                    .copyTo(FileOutputStream("${it.result as String}${File.separator}paper.html"))
                thisClassLoader.getResourceAsStream("assets/online_question.js")
                    .copyTo(FileOutputStream("${it.result as String}${File.separator}common${File.separator}components${File.separator}online_question.js"))
            })
        }
    }
}