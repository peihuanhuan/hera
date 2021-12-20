package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class NotifyUserMessageHandler() : AbstractMessageHandler {

    override fun receivedMessages(): List<String> {
        return listOf("拍一拍小助理的脑袋")
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        wxMpXmlMessage.replyKfMessage("小主，我可以在两天内提醒您消息啦~")
        return null
    }

}