package net.peihuan.hera

import mu.KotlinLogging
import net.peihuan.hera.constants.ActEnum
import net.peihuan.hera.feign.service.ZyService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


class ZyServiceTest : HeraApplicationTests() {

    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var zyService: ZyService


    @Test
    fun orderService() {

        val queryActs = zyService.queryActs()
        println(queryActs.data)
        val queryOrders = zyService.queryOrders(null, null, "2021-11-01 00:00:00", "2021-12-05 00:00:00")

        val queryActsaa = zyService.queryActLink("aaaaa", ActEnum.WM.key, 1)


        log.info { queryActsaa }

    }

}