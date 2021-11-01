package net.peihuan.hera

import net.peihuan.hera.util.buildALabel
import org.junit.jupiter.api.Test

class SimpleTest {

    @Test
    fun test() {
        val x = """
            前置
            
            第一部分from-----<a>第一部分</a>---第一部分
            第2部分from-----<a>第2部分</a>---第2部分
            
            
            
            后置
            
            """.trimIndent()

        val regex = Regex("^.*<a>(.*)<\\/a>.*\$")
        val match = regex.matches(x)

        println(match)

        val url = "http://baidu.com"
        var replace = x.replaceFirst("<a>(.*)<\\/a>".toRegex(), buildALabel(url, "$1"))
        replace = replace.replaceFirst("<a>(.*)<\\/a>".toRegex(), buildALabel("http://taobao", "$1"))
        println(replace)
    }
}