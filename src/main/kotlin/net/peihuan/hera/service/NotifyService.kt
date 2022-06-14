package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.subscribe.WxMpSubscribeMessage
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage
import mu.KotlinLogging
import net.peihuan.hera.config.WxMpProperties
import net.peihuan.hera.config.property.HeraProperties
import net.peihuan.hera.constants.NotifyTypeEnum
import net.peihuan.hera.constants.YYYY_MM_DD_HH_MM_SS
import net.peihuan.hera.constants.ZyOrderSourceEnum
import net.peihuan.hera.constants.ZyOrderSourceEnum.Companion.getSourceEnum
import net.peihuan.hera.domain.BilibiliTask
import net.peihuan.hera.feign.service.PushPlusService
import net.peihuan.hera.persistent.po.BilibiliTaskPO
import net.peihuan.hera.persistent.po.ZyOrderPO
import net.peihuan.hera.util.replyKfMessage
import org.joda.time.DateTime
import org.springframework.stereotype.Service


@Service
class NotifyService(
    private val wxMpService: WxMpService,
    private val heraProperties: HeraProperties,
    private val pushPlusService: PushPlusService,
    private val wxMpProperties: WxMpProperties
) {

    private val log = KotlinLogging.logger {}

    val pushPlustoken = "a7643cc0de74425e8d8ce69e885100bb"

    fun notifyOrderStatusToUser(order: ZyOrderPO, presentPoints: Int? = null) {
        val templateMessage = WxMpTemplateMessage.builder()
            .toUser(order.openid)
            .templateId(wxMpProperties.orderStatusTemplateid)
            .build()

        val remark = when (order.source) {
            ZyOrderSourceEnum.BUY.code -> "小主，您的订单已完成，赠送您 $presentPoints 积分，可兑换其它会员"
            ZyOrderSourceEnum.EXCHANGE.code -> "小主，您兑换的权益已送达，连续签到更有惊喜哦~"
            ZyOrderSourceEnum.PRESENT.code -> "小主，赠送您的权益已送达，请查收哦~"
            else -> ""
        }

        val actualOrderAmountStr = when (order.source) {
            ZyOrderSourceEnum.BUY.code -> order.actualOrderAmountStr
            else -> "0"
        }

        templateMessage
            .addData(WxMpTemplateData("first", "您的订单已完成"))
            .addData(WxMpTemplateData("keyword1", order.outTradeNo))
            .addData(WxMpTemplateData("keyword2", actualOrderAmountStr))
            .addData(WxMpTemplateData("keyword3", order.name))
            .addData(WxMpTemplateData("keyword4", order.pay_at))
            .addData(WxMpTemplateData("remark", remark, "#F54B27"))

        wxMpService.templateMsgService.sendTemplateMsg(templateMessage)
    }

    fun notifyOrderStatusToAdmin(order: ZyOrderPO) {
        val request = PushPlusService.SendRequest(
            token = pushPlustoken,
            title = "${order.name} 已完成",
            channel = "webhook",
            webhook = "pushplus",
            content = """
                    支付金额：${order.actualOrderAmountStr}
                    订单号：${order.outTradeNo}
                    来源: ${getSourceEnum(order.source!!)!!.msg}
                    支付时间：${order.pay_at}
                    """.trimMargin()
        )
        val send = pushPlusService.send(request)
        log.info { send }
    }

    fun notifyLeaveMessage(openid: String, content: String?) {
        val request = PushPlusService.SendRequest(
            token = pushPlustoken,
            title = "有新的聊天，请及时回复：$content",
            channel = "webhook",
            webhook = "pushplus",
            content = """
                    用户：${openid}
                    时间：${DateTime.now().toString(YYYY_MM_DD_HH_MM_SS)}
                    内容：$content
                    客服：https://mpkf.weixin.qq.com
                    """.trimMargin()
        )
        val send = pushPlusService.send(request)
    }


    fun notifyAdmin(message: String) {
        val request = PushPlusService.SendRequest(
            token = pushPlustoken,
            title = message,
            channel = "webhook",
            webhook = "pushplus",
            content = message
        )
        val send = pushPlusService.send(request)
    }

    fun notifyTaskTooLong(task: BilibiliTaskPO) {
        val request = PushPlusService.SendRequest(
            token = pushPlustoken,
            title = "有任务长时间没处理：${task.name}",
            channel = "webhook",
            webhook = "pushplus",
            content = """
                    创建时间：${DateTime(task.createTime).toString(YYYY_MM_DD_HH_MM_SS)}
                    个数：${task.size}
                    """.trimMargin()
        )
        val send = pushPlusService.send(request)
    }

    fun notifyTaskFail(task: BilibiliTask) {
        val request = PushPlusService.SendRequest(
            token = pushPlustoken,
            title = "任务处理失败：${task.name}",
            channel = "webhook",
            webhook = "pushplus",
            content = """
                    名称：${task.name}
                    个数：${task.subTaskSize}
                    """.trimMargin()
        )
        val send = pushPlusService.send(request)
    }

    fun notifyTaskResult(task: BilibiliTask) {
        if (task.notifyType == NotifyTypeEnum.MESSAGE_TEMPLATE) {
            val templateMessage = WxMpSubscribeMessage.builder()
                .toUser(task.openid)
                .page("https://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=Mzg3OTY5MjA2NQ==&scene=124#wechat_redirect")
                .dataMap(
                    mapOf(
                        "thing1" to task.name,
                        "phrase2" to "任务已完成",
                        "thing3" to "公众号回复【音频】，获取提取结果"
                    )
                )
                .templateId(wxMpProperties.taskSubscribeId)
                .build()

            wxMpService.subscribeMsgService.send(templateMessage)
        } else if (task.notifyType == NotifyTypeEnum.MP_REPLY){
            task.openid.replyKfMessage(task.result)
        }
    }

}