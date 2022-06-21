package net.peihuan.hera.service

import mu.KotlinLogging
import net.peihuan.hera.HeraApplicationTests
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class RedPackageCoverServiceTest : HeraApplicationTests() {

    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var redPackageCoverService: RedPackageCoverService

    @Test
    fun redPackage() {
        // redPackageService.giveUpPackage("fake", 1)
    }

}