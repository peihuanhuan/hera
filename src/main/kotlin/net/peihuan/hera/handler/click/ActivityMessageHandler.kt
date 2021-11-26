package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.HeraProperties
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.service.ChannelService
import net.peihuan.hera.util.buildKfImage
import org.springframework.stereotype.Component

@Component
class ActivityMessageHandler(private val wxMpService: WxMpService,
                             private val zyProperties: ZyProperties,
                             private val channelService: ChannelService,
                             private val heraProperties: HeraProperties) : AbstractMenuAndMessageHandler() {

    companion object {
        const val receivedMessage = "xxxxx"
    }

    override fun showMsg(): String {
        return "useless"
    }

    override fun reply(): String {
        return receivedMessage
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "exchangeMember"
    }

    override fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        sendMessage(wxMpXmlMessage)
        return null
    }

    override fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        sendMessage(wxMpXmlMessage)
        return null
    }

    private fun sendMessage(wxMpXmlMessage: WxMpXmlMessage) {

        wxMpService.kefuService.sendKefuMessage(buildKfImage(wxMpXmlMessage, heraProperties.wechatMediaid));
    }


}