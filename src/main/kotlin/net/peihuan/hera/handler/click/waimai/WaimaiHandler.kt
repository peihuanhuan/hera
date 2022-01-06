package net.peihuan.hera.handler.click.waimai

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.MM_DD
import net.peihuan.hera.constants.ZyOrderSourceEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.handler.click.AbstractMenuHandler
import net.peihuan.hera.service.ChannelService
import net.peihuan.hera.util.ZyUtil
import net.peihuan.hera.util.buildText
import net.peihuan.hera.util.completeALable
import org.joda.time.DateTime
import org.springframework.stereotype.Component


@Component
class WaimaiHandler(
        private val zyProperties: ZyProperties,
        private val cacheManage: CacheManage,
        private val channelService: ChannelService,
) : AbstractMenuHandler {

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "waimai"
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val channel = channelService.getChannelOrCreate(wxMpXmlMessage.fromUser, ZyOrderSourceEnum.BUY)
        val wmUrl = ZyUtil.buildWmUrl(channel.id, zyProperties.appid)
        val content = """
            小主，您的外卖红包来了~
            
            ${DateTime.now().toString(MM_DD)}的红包已更新！
            
            ${"<a>👉 美团、饿了么外卖红包~</a>".completeALable(wmUrl)}

        """.trimIndent()
        return buildText(content, wxMpXmlMessage)
    }

}