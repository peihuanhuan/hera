package net.peihuan.hera

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.kefu.request.WxMpKfAccountRequest
import mu.KotlinLogging
import net.peihuan.hera.config.HeraProperties
import net.peihuan.hera.config.WxMpProperties
import net.peihuan.hera.persistent.po.ZyOrderPO
import net.peihuan.hera.persistent.service.ConfigPOService
import net.peihuan.hera.service.NotifyService
import net.peihuan.hera.util.toJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.File


class WxConfigServiceTest : HeraApplicationTests() {

    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var configPOService: ConfigPOService

    @Autowired
    lateinit var wxMpService: WxMpService

    @Autowired
    lateinit var heraProperties: HeraProperties

    @Autowired
    lateinit var wxMpProperties: WxMpProperties

    @Autowired
    lateinit var notifyService: NotifyService

    @Test
    fun uploadPic() {
        val mediaImgUpload = wxMpService.materialService.mediaUpload(WxConsts.MaterialType.IMAGE, File("/Users/peihuan/Downloads/000000000.jpeg"))
        val materialFileBatchGet = wxMpService.materialService.materialCount()

        log.info { mediaImgUpload.toJson() }
    }

    @Test
    fun addKf() {
        val request = WxMpKfAccountRequest("main", "阿烫", "kun_jin_kao")

        val kfAccountAdd = wxMpService.kefuService.kfAccountAdd(request)

        log.info { kfAccountAdd.toJson() }
    }

    @Test
    fun notifyTest() {

        val order = ZyOrderPO()
        order.openid = heraProperties.adminOpenid
        order.name = "商品名称"
        order.outTradeNo = "111111"
        order.pay_at = "今天"
        order.actualOrderAmountStr = "100"
        notifyService.notifyOrderStatusToUser(order ,100)
        notifyService.notifyLeaveMessage("11", "aaaaaaaaaaaaaaaaaaaaaaaaaaabbbbddasdasd")

        println()
    }

    @Test
    fun createQrcode() {
        val qrCodeCreateLastTicket = wxMpService.qrcodeService.qrCodeCreateLastTicket("ceshi")
        val file = wxMpService.qrcodeService.qrCodePicture(qrCodeCreateLastTicket)

    }
}