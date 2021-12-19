package net.peihuan.hera.util

import java.io.FileReader
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


object JsUtil {

    //执行 h(m('aaa'))
    fun execJs(accessToken: String): String {
        val manager = ScriptEngineManager()
        val engine: ScriptEngine = manager.getEngineByName("javascript")
        val fileReader = FileReader("src/main/resources/js/en.js")
        engine.eval(fileReader)
        val invokeFunction = (engine as Invocable).invokeFunction("m", accessToken)
        return (engine as Invocable).invokeFunction("h", invokeFunction) as String
    }

}
