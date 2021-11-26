package net.peihuan.hera

import net.peihuan.hera.feign.service.FastposterService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HeraApplicationTests {

    @Autowired
    private lateinit var fastposterService: FastposterService


    @Test
    fun test() {
        val queryUrl =
            fastposterService.queryUrl(
                FastposterService.Request("4", "https://www.baidu.com"))


        println(queryUrl)
    }

}
