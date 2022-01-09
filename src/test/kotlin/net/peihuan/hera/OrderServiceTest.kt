package net.peihuan.hera

import mu.KotlinLogging
import net.peihuan.hera.service.OrderService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


class OrderServiceTest : HeraApplicationTests() {

    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var orderService: OrderService


    @Test
    fun orderService() {
        orderService.order(1)
    }



}