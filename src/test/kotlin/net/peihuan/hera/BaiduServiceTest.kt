package net.peihuan.hera

import mu.KotlinLogging
import net.peihuan.baiduPanSDK.service.BaiduService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


class BaiduServiceTest : HeraApplicationTests() {

    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var baiduService: BaiduService

    @Test
    fun  xx() {
        val x = baiduService.getAuthorizeUrl("http://www.baidu.com")
    }




}