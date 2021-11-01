package net.peihuan.hera

import me.chanjar.weixin.mp.api.WxMpService
import net.peihuan.hera.persistent.service.ConfigPOService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


class ConfigPOServiceTest :HeraApplicationTests() {

    @Autowired
    lateinit var configPOService: ConfigPOService
    @Autowired
    lateinit var wxMpService: WxMpService

    @Test
    fun test() {
        // val mediaImgUpload = wxMpService.materialService.mediaUpload(WxConsts.MaterialType.IMAGE, File("/Users/peihuan/Downloads/000000000.jpeg"))
        val materialFileBatchGet = wxMpService.materialService.materialCount()

        println()
    }
}