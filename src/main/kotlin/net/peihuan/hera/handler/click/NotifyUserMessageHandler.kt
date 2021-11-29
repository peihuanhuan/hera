package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.service.UserPointsService
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class NotifyUserMessageHandler(val userPointsService: UserPointsService) : AbstractMenuAndMessageHandler() {

    companion object {
        const val reply = "拍一拍小助理的脑袋"
    }

    override fun showMsg(): String {
        return "use less"
    }

    override fun reply(): String {
        return reply
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "xxxx"
    }

    override fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        wxMpXmlMessage.replyKfMessage("小主，我可以在两天内提醒您消息啦~")
        return null
    }

    override fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        wxMpXmlMessage.replyKfMessage("小主，我可以在两天内提醒您消息啦~")
        return null
    }

}