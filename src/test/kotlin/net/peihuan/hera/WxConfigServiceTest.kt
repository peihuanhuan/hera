package net.peihuan.hera

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.kefu.request.WxMpKfAccountRequest
import me.chanjar.weixin.mp.bean.material.WxMpMaterial
import mu.KotlinLogging
import net.peihuan.hera.component.JwtTokenComponent
import net.peihuan.hera.config.WxMpProperties
import net.peihuan.hera.config.property.HeraProperties
import net.peihuan.hera.constants.BilibiliTaskOutputTypeEnum
import net.peihuan.hera.constants.BilibiliTaskSourceTypeEnum
import net.peihuan.hera.constants.NotifyTypeEnum
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.domain.BilibiliTask
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

    @Autowired
    lateinit var jwtTokenComponent: JwtTokenComponent

    @Test
    fun login() {
        val x = jwtTokenComponent.generateUserToken(1, "NORMAL_USER")
        println(x)
    }

    @Test
    fun uploadPic() {
        val file = File("/Users/peihuan/Downloads/0000000000000.jpeg")
        // val mediaImgUpload = wxMpService.materialService.mediaImgUpload(file)
        // val materialFileBatchGet = wxMpService.materialService.materialCount()

        val wxMpMaterial = WxMpMaterial()
        wxMpMaterial.name = "客服账号"
        wxMpMaterial.file = file
        // val materialFileUpload =
            // wxMpService.materialService.materialFileUpload(WxConsts.MaterialType.IMAGE, wxMpMaterial)

        val materialCount = wxMpService.materialService.materialFileBatchGet(WxConsts.MaterialType.IMAGE, 0 ,11)

        log.info {  }

        // log.info { mediaImgUpload.toJson() }
    }

    @Test
    fun addKf() {
        val request = WxMpKfAccountRequest("main", "阿烫", "kun_jin_kao")

        val kfAccountAdd = wxMpService.kefuService.kfAccountAdd(request)

        log.info { kfAccountAdd.toJson() }
    }


    @Test
    fun notifyTest2() {

        val task = BilibiliTask(
            name = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            result = "www.baidu.com",
            request = "adasdasdasd",
            openid = "oIWc_51xYURq_7jNCfrr40dc0q3Q",
            status = TaskStatusEnum.DEFAULT,
            type = BilibiliTaskSourceTypeEnum.MULTIPLE,
            outputType = BilibiliTaskOutputTypeEnum.AUDIO,
            notifyType = NotifyTypeEnum.MP_REPLY
        )
        notifyService.notifyTaskResult(task)

        println()
    }

    @Test
    fun notifyTest() {

        val order = ZyOrderPO()
        order.openid = heraProperties.adminOpenid
        order.name = "商品名称"
        order.outTradeNo = "111111"
        order.pay_at = "今天"
        order.actualOrderAmountStr = "100"
        notifyService.notifyOrderStatusToUser(order)
        notifyService.notifyLeaveMessage("11", "aaaaaaaaaaaaaaaaaaaaaaaaaaabbbbddasdasd")

        println()
    }

    @Test
    fun createQrcode() {
        val qrCodeCreateLastTicket = wxMpService.qrcodeService.qrCodeCreateLastTicket("ceshi")
        val file = wxMpService.qrcodeService.qrCodePicture(qrCodeCreateLastTicket)

    }
}