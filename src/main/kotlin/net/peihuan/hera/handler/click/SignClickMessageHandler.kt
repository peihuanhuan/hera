package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.service.SignService
import net.peihuan.hera.util.buildText
import org.springframework.stereotype.Component

@Component
class SignClickMessageHandler(val signService: SignService) : AbstractMenuAndMessageHandler() {

    companion object {
        const val reply = "签到"
    }

    override fun showMsg(): String {
        return "use less"
    }

    override fun reply(): String {
        return reply
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "sign"
    }

    override fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        return buildText(signService.sign(wxMpXmlMessage.fromUser), wxMpXmlMessage)
    }

    override fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage {
        return buildText(signService.sign(wxMpXmlMessage.fromUser), wxMpXmlMessage)
    }


}