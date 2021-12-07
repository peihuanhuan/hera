package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.service.ChannelService
import net.peihuan.hera.util.buildText
import net.peihuan.hera.util.completeALable
import org.springframework.stereotype.Component


@Component
class HuafeiHandler(
        private val zyProperties: ZyProperties,
        private val channelService: ChannelService,
        private val cacheManage: CacheManage,
) : AbstractMenuAndMessageHandler() {
    override fun showMsg(): String {
        return "➜ 戳我了解低价话费"
    }

    override fun reply(): String {
        return "戳你啦！"
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "huafei"
    }

    override fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage {
        val channel = channelService.getChannelOrCreate(wxMpXmlMessage.fromUser).id
        val url = "https://cdn.wxthe.com/life/#/pages/act/phone?appid=${zyProperties.appid}&channel=$channel"
        val huafeiContent = cacheManage.getBizValue(BizConfigEnum.HUAFEI)
        return buildText(huafeiContent.completeALable(url), wxMpXmlMessage)
    }
}