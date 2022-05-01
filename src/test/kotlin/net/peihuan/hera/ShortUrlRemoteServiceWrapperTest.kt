package net.peihuan.hera

import mu.KotlinLogging
import net.peihuan.hera.service.remote.ShortUrlRemoteServiceWrapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


class ShortUrlRemoteServiceWrapperTest : HeraApplicationTests() {

    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var shortUrlFeignService: ShortUrlRemoteServiceWrapper


    @Test
    fun generateShortUrl_test() {
        val x = shortUrlFeignService.getShortUrl("https://bilibili-audio.oss-cn-beijing.aliyuncs.com/20%E5%B2%81%E5%88%B030%E5%B2%81%EF%BC%8C%E6%98%AF%E4%BD%A0%E6%9C%80%E4%B8%8D%E5%8F%AF%E6%8C%A5%E9%9C%8D%E7%9A%84%E5%85%89%E9%98%B4%EF%BC%88T%E5%90%9B%E8%AF%91%EF%BC%89.mp3")
        println(x)
    }



}