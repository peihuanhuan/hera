package net.peihuan.hera.handler.click.member

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.handler.click.AbstractMenuAndMessageHandler
import net.peihuan.hera.service.ChannelService
import net.peihuan.hera.util.ZyUtil.Companion.buildAllProductUrl
import net.peihuan.hera.util.buildALabel
import net.peihuan.hera.util.buildText
import org.springframework.stereotype.Component

@Component
class AllProductMessageHandler(
    private val zyProperties: ZyProperties,
    private val channelService: ChannelService
) : AbstractMenuAndMessageHandler() {


    override fun receivedMessages(): List<String> {
        return listOf("全网低价会员")
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "allProduct"
    }



    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val channelId = channelService.getChannelOrCreate(wxMpXmlMessage.fromUser).id
        val url = buildAllProductUrl(channelId, zyProperties.appid)
        return buildText(buildALabel(url, "➜ 全网六十种超低会员，戳我购买！"), wxMpXmlMessage)
    }


}