package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import mu.KotlinLogging
import net.peihuan.hera.config.HeraProperties
import net.peihuan.hera.config.WxMpProperties
import net.peihuan.hera.constants.YYYY_MM_DD_HH_MM_SS
import net.peihuan.hera.feign.service.PushPlusService
import net.peihuan.hera.persistent.po.ZyOrderPO
import org.joda.time.DateTime
import org.springframework.stereotype.Service


@Service
class NotifyService(private val wxMpService: WxMpService,
                    private val heraProperties: HeraProperties,
                    private val pushPlusService: PushPlusService,
                    private val wxMpProperties: WxMpProperties) {

    private val log = KotlinLogging.logger {}

    val token = "a7643cc0de74425e8d8ce69e885100bb"

    fun notifyOrderStatus(order: ZyOrderPO) {
        val request = PushPlusService.SendRequest(
                token = token,
                title = "${order.name} 已完成",
                channel = "webhook",
                webhook = "pushplus",
                content = """
                    支付金额：${order.actualOrderAmountStr}
                    订单号：${order.outTradeNo}
                    支付时间：${order.pay_at}
                    """.trimMargin())
        val send = pushPlusService.send(request)
        log.info { send }
    }

    fun notifyLeaveMessage(openid: String, content: String) {
        val request = PushPlusService.SendRequest(
                token = token,
                title = "有新的聊天，请及时回复：$content",
                channel = "webhook",
                webhook = "pushplus",
                content = """
                    用户：${openid}
                    时间：${DateTime.now().toString(YYYY_MM_DD_HH_MM_SS)}
                    内容：$content
                    客服：https://mpkf.weixin.qq.com
                    """.trimMargin())
        val send = pushPlusService.send(request)
        log.info { send }
    }

}