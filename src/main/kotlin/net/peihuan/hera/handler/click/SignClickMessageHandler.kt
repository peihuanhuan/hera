package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.service.SignService
import net.peihuan.hera.util.buildText
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class SignClickMessageHandler(val signService: SignService) : AbstractMenuAndMessageHandler() {

    override fun receivedMessages(): List<String> {
        return listOf("签到")
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "sign"
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage {
        wxMpXmlMessage.replyKfMessage("由于成本压力，正在逐渐降低签到得到的积分，请老用户尽快兑换积分。\n目前购买会员已支持即时返现，返现金额不低于0.3元，根据订单金额计算。金额过小订单不会返现。")
        return buildText(signService.sign(wxMpXmlMessage.fromUser), wxMpXmlMessage)
    }

}