package net.peihuan.hera

import net.peihuan.hera.constants.YYYY_MM_DD_HH_MM_SS
import net.peihuan.hera.util.buildALabel
import net.peihuan.hera.util.completeMsgMenu
import org.apache.commons.io.FilenameUtils
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import java.io.File

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

        val completeMsgMenu = x.completeMsgMenu("aa")
        val url = "http://baidu.com"
        var replace = x.replaceFirst("<a>(.*)<\\/a>".toRegex(), buildALabel(url, "$1"))
        replace = replace.replaceFirst("<a>(.*)<\\/a>".toRegex(), buildALabel("http://taobao", "$1"))
        println(replace)
    }


    @Test
    fun testFFmpge() {
        println(DateTime(2022,6,16,21,0).toString(YYYY_MM_DD_HH_MM_SS))
    }

    @Test
    fun tesss() {
        val file = File("/Users/peihuan/Downloads/20220110163524.xlsx")
        val fakeFile =
            File(FilenameUtils.getFullPath(file.absolutePath) + FilenameUtils.getBaseName(file.absolutePath) + "-fake.xlsx")
        val xx = File("/Users/peihuan/Downloads/20220110163524-fake2.xlsx")
        // 添加图片的魔数
        fakeFile.writeBytes(byteArrayOf(
            "ff".toInt(16).toByte(),
            ("d8".toInt(16).toByte()),
            ("ff".toInt(16).toByte()))
        )
        fakeFile.appendBytes(file.readBytes())
    }
}