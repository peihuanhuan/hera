package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.service.SignService
import net.peihuan.hera.util.buildText
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
        return buildText(signService.sign(wxMpXmlMessage.fromUser), wxMpXmlMessage)
    }

}