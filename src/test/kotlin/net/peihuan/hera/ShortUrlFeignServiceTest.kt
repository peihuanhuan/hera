package net.peihuan.hera

import mu.KotlinLogging
import net.peihuan.hera.service.ToolService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


class ShortUrlFeignServiceTest : HeraApplicationTests() {

    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var toolService: ToolService


    @Test
    fun short() {
        val shortUrl =
            toolService.tryShortUrl("https://bilibili-audio.oss-cn-beijing.aliyuncs.com/%E3%80%8C%E5%A4%8F%E3%81%AB%E6%95%A3%E3%82%8B%EF%BC%88%E5%9C%A8%E5%A4%8F%E5%A4%A9%E5%87%8B%E8%B0%A2%EF%BC%89++%E5%88%9D%E9%9F%B3%E3%83%9F%E3%82%AF%E3%80%90o+k+a%E3%80%91+p1+%E5%A4%8F%E3%81%AB%E6%95%A3%E3%82%8B+_+%E5%88%9D%E9%9F%B3%E3%83%9F%E3%82%AF+_+o+k+a.mp3%E3%80%8D%E7%AD%895%E4%B8%AA%E6%96%87%E4%BB%B6.zip")
        println(shortUrl)
    }


}