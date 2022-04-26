package net.peihuan.hera

import mu.KotlinLogging
import net.peihuan.hera.constants.BilibiliTaskOutputTypeEnum
import net.peihuan.hera.constants.BilibiliTaskSourceTypeEnum
import net.peihuan.hera.constants.NotifyTypeEnum
import net.peihuan.hera.service.BVideo2AudioService
import net.peihuan.hera.service.BilibiliService
import net.peihuan.hera.util.CmdUtil
import net.peihuan.hera.util.doDownload
import net.peihuan.hera.util.doDownloadBilibiliVideo
import net.peihuan.hera.util.getLocationUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.io.File
import java.util.regex.Pattern


class BilibiliServiceTest : HeraApplicationTests() {

    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var bilibiliService: BilibiliService
    @Autowired
    lateinit var bVideo2AudioService: BVideo2AudioService

    @Test
    fun xx() {
        val dashAudioPlayUrl = bilibiliService.getDashAudioPlayUrl("26587943", "45721859")
    }

    @Test
    fun testProcessVideos(){
        bVideo2AudioService.ffmpeg("/Users/peihuan/Downloads/479516842_nb2-1-30280.m4s", "/Users/peihuan/Downloads/b3.mp3",11)
    }

    @Test
    fun testSaveDB() {
        SecurityContextHolder.getContext().authentication = PreAuthenticatedAuthenticationToken("fake-3","",null)
        bVideo2AudioService.saveTask("""
            https://m.bilibili.com/audio/au2831765?xxx=1
        """.trimMargin(), BilibiliTaskSourceTypeEnum.FREE, BilibiliTaskOutputTypeEnum.VIDEO, NotifyTypeEnum.MP_REPLY)
    }

    @Test
    fun orderService() {

        val viewByBvid = bilibiliService.getViewByBvid("BV17g411N7L9")
        val view = bilibiliService.getViewByAid("506850749")
        log.info { view }

        val locationUrl = getLocationUrl("https://b23.tv/CYkD9MT")


        val dashPlayurl = bilibiliService.getDashAudioPlayUrl(avid = "506850749", cid = "445902927")

        val headers = mapOf(
            "Accept" to "*/*",
            "Accept-Encoding"  to "gzip, deflate, br",
            "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7",
            "Connection" to "keep-alive",
            "origin" to "https://www.bilibili.com",
            "referer" to "https://www.bilibili.com/video/BV17g411N7L9",
            "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36")


        log.info { dashPlayurl }

        val source = "/Users/peihuan/Downloads/test-audio.m4s"
        log.info { "====== 开始下载 m4s" }
        doDownload(dashPlayurl.first()!!, File(source), headers)

        val target = "/Users/peihuan/Downloads/test-audio.mp3"
        File(target).delete()
        log.info { "====== 开始转 mp3" }
        CmdUtil.executeBash("ffmpeg  -i $source $target")

    }


    @Test
    fun regTest() {
        val data = """
            
            https://www.bilibili.com/video/BV1tF411z7Gn?spm_id_from=333.851.b_7265636f6d6d656e64.1
            
            https://www.bilibili.com/video/BV111111?p=1&a=3 

            https://www.bilibili.com/video/BV111112?p=2 8

            https://www.bilibili.com/video/BV111113?a=1&p=3 
            

            https://www.bilibili.com/video/BV111114

            https://www.bilibili.com/video/BV111115?a=3

            https://www.bilibili.com/video/BV111116?a=1&p=6&x=3

            https://www.bilibili.com/video/BV11117?
        """.trimIndent()
        val bvids = mutableListOf<String>()
        val regex = Pattern.compile("(http|https):\\/\\/www\\.bilibili\\.com\\/video\\/(BV\\w*)(\\?((.*&p=|p=|)(\\d+)\\S*|\\S*))?\\s*").matcher(data)
        var matchStart = 0
        while (regex.find(matchStart)) {
            bvids.add(regex.group(2) + " " + regex.group(6))
            matchStart = regex.end()
        }
        println(bvids)

        // val find = BVReg.find("dasdasdasdasda]https://www.bilibili.com/video/BV12321?p=1&a=1") ?: return
        // val groups = find.groups
        // log.info { "==== 解析到的 BV 为 ${groups[2]!!.value}" }
    }


    @Test
    fun regTest2() {
        val shortUrlReg = Regex("((http|https):\\/\\/b23\\.tv\\/\\w*)(\\?.*)? ?.*")
        val xx = Pattern.compile("((http|https):\\/\\/b23\\.tv\\/\\w*)(\\?.*)? ?.*?").matcher("【一个敢说，一个敢做-哔哩哔哩】 https://b23.tv/CYkD9MT kkk   【一个敢说，一个敢做-哔哩哔哩】 https://b23.tv/avasd kkk")
        var matchStart = 0
        while (xx.find(matchStart)) {
            log.info { xx.group(1) }
            matchStart = xx.end()
        }
        // val find = shortUrlReg.find("【一个敢说，一个敢做-哔哩哔哩】 https://b23.tv/CYkD9MT kkk") ?: return
        // val groups = find.groups
        // log.info { "==== 解析到的 短链 为 ${groups[1]!!.value}" }
    }

    @Test
    fun test1() {
        val x = bilibiliService.resolve2BilibiliVideos("""
            【开场一句就能俘获你芳心！《金玉良缘》笛子/竹笛版-哔哩哔哩】 https://b23.tv/gF0xd6W
            
            https://www.bilibili.com/audio/au2831765?xxx=1

            https://www.bilibili.com/festival/2022bnj?bvid=BV1Ga41127eH&spm_id_from=333.788.top_right_bar_window_custom_collection.content.click

            https://www.bilibili.com/video/BV1ua411872R
        """.trimIndent())
        println("====")
        println(x)
    }

    @Test
    fun testResolveBv() {
        val resolveBV = bilibiliService.resolve2BilibiliVideos("【开场一句就能俘获你芳心！《金玉良缘》笛子/竹笛版-哔哩哔哩】 https://b23.tv/gF0xd6W")
        log.info { "==== bv 为 $resolveBV" }
    }


    @Test
    fun download() {
        var url = bilibiliService.getDashAudioPlayUrl("96649838", "164996045")
        doDownloadBilibiliVideo(url.first()!!,
            File("/Users/peihuan/Downloads/aa.m4s"),
            "BV117411f73L")
    }
}