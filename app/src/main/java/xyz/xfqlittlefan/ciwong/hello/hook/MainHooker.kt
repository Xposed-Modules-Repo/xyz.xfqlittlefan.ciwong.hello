package xyz.xfqlittlefan.ciwong.hello.hook

import androidx.annotation.Keep
import androidx.preference.PreferenceManager
import de.robv.android.xposed.XposedHelpers
import org.json.JSONObject
import xyz.xfqlittlefan.ciwong.hello.hook.base.BaseHooker
import xyz.xfqlittlefan.ciwong.hello.hook.util.AppPreferences
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

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

        "com.ciwong.epaper.modules.me.bean.WorkContents".findAndHookMethod("getExamMode",
            condition = { sharedPreferences.getBoolean(AppPreferences.NO_EXAM, true) },
            doBefore = {
                it.result = 0
            })

        "com.ciwong.epaper.modules.epaper.a.b$4".findAndHookMethod("success", condition = {
            sharedPreferences.getBoolean(
                AppPreferences.SHOW_ANSWERS_LISTEN_SPEAK, true
            )
        }, doBefore = { param ->
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
            LogUtil.log("Listen speak exam process finished!")
            param.args[0] = result

        }, parameterTypes = arrayOf(Any::class.java)
        )


        "com.ciwong.epaper.util.m".findAndHookMethod("e", doAfter = {
            val thisClassLoader =
                this@MainHooker::class.java.classLoader ?: throw HelloCiwongImpossibleException(
                    "HelloCiwong has got a strange problem 0x1."
                )

            val pathPaperDotHtml = "${it.result as String}${File.separator}paper.html"
            val pathOnlineQuestionDotJs =
                "${it.result as String}${File.separator}common${File.separator}components${File.separator}online_question.js"

            if (sharedPreferences.getBoolean(
                    AppPreferences.SHOW_ANSWERS_ONLINE_PAPER, true
                )
            ) {
                LogUtil.log("Ciwong, show the answer!")
                if (!File("$pathPaperDotHtml.original").exists()) {
                    LogUtil.log("paper.html not found, replacing...")
                    File(pathPaperDotHtml).renameTo(File("$pathPaperDotHtml.original"))
                    thisClassLoader.getResourceAsStream("assets/paper.html")
                        .copyTo(FileOutputStream(pathPaperDotHtml))
                    LogUtil.log("paper.html has been successfully replaced!")
                }

                if (!File("$pathOnlineQuestionDotJs.original").exists()) {
                    LogUtil.log("online_question.js not found, replacing...")
                    File(pathOnlineQuestionDotJs).renameTo(File("$pathOnlineQuestionDotJs.original"))
                    thisClassLoader.getResourceAsStream("assets/online_question.js")
                        .copyTo(FileOutputStream(pathOnlineQuestionDotJs))
                    LogUtil.log("online_question.js has been successfully replaced!")
                }
            } else {
                LogUtil.log("Ciwong, don't show the answer... 😣")
                if (File("$pathPaperDotHtml.original").exists()) {
                    LogUtil.log("Please don't give me the replaced paper.html...")
                    val delete = File(pathPaperDotHtml).delete()
                    val rename = File("$pathPaperDotHtml.original").renameTo(File(pathPaperDotHtml))
                    if (!delete || !rename) {
                        LogUtil.log("Something went wrong when restoring paper.html. delete: $delete, rename: $rename.")
                    } else {
                        LogUtil.log("paper.html has been successfully restored.")
                    }
                }

                if (File("$pathOnlineQuestionDotJs.original").exists()) {
                    LogUtil.log("Please don't give me the replaced online_question.js...")
                    val delete = File(pathOnlineQuestionDotJs).delete()
                    val rename = File("$pathOnlineQuestionDotJs.original").renameTo(
                        File(pathOnlineQuestionDotJs)
                    )
                    if (!delete || !rename) {
                        LogUtil.log("Something went wrong when restoring online_question.js. delete: $delete, rename: $rename.")
                    } else {
                        LogUtil.log("online_question.js has been successfully restored.")
                    }
                }
            }
        })
    }
}