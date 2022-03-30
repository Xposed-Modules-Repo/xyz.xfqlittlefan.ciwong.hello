package xyz.xfqlittlefan.ciwong.hello.hook

import androidx.annotation.Keep
import de.robv.android.xposed.XposedHelpers
import org.json.JSONObject
import xyz.xfqlittlefan.ciwong.hello.hook.base.BaseHooker
import xyz.xfqlittlefan.ciwong.hello.hook.base.MethodHook
import xyz.xfqlittlefan.ciwong.hello.hook.base.MethodReplacement
import xyz.xfqlittlefan.ciwong.hello.throwable.HelloCiwongImpossibleException
import xyz.xfqlittlefan.ciwong.hello.util.LogUtil
import java.io.File
import java.io.FileOutputStream

@Keep
class MainHooker : BaseHooker() {
    override fun onPackageLoaded() {
        findAndHookMethod("com.ciwong.epaper.modules.epaper.a.b$4",
            "success",
            Any::class.java,
            object : MethodHook() {
                override fun doBeforeHookedMethod(param: MethodHookParam) {
                    if ((XposedHelpers.getObjectField(
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
                }
            })

        findAndHookMethod("com.ciwong.epaper.modules.me.bean.WorkContents",
            "getExamMode",
            object : MethodReplacement() {
                override fun replacedMethod(param: MethodHookParam?) = 0
            })

        findAndHookMethod("com.ciwong.epaper.util.m", "e", object : MethodHook() {
            override fun doAfterHookedMethod(param: MethodHookParam) {
                LogUtil.log("Ciwong, show the answer!")

                val thisClassLoader =
                    this@MainHooker::class.java.classLoader ?: throw HelloCiwongImpossibleException(
                        "HelloCiwong has got a strange problem 0x1."
                    )

                thisClassLoader.getResourceAsStream("assets/paper.html")
                    .copyTo(FileOutputStream("${param.result as String}${File.separator}paper.html"))
                thisClassLoader.getResourceAsStream("assets/online_question.js")
                    .copyTo(FileOutputStream("${param.result as String}${File.separator}common${File.separator}components${File.separator}online_question.js"))
            }
        })
    }
}