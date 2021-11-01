package net.peihuan.hera.handler.click.waimai

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.MM_DD
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.handler.click.AbstractMenuHandler
import net.peihuan.hera.util.buildMsgMenuUrl
import net.peihuan.hera.util.buildText
import org.joda.time.DateTime
import org.springframework.stereotype.Component


@Component
class WaimaiHandler(
        private val zyProperties: ZyProperties,
        private val cacheManage: CacheManage,
) : AbstractMenuHandler() {

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "waimai"
    }

    override fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val content = """
            小主，您的外卖红包来了~
            ${DateTime.now().toString(MM_DD)}的外卖红包已更新！
            
            🔜${buildMsgMenuUrl(MeituanWmHandler.reply, MeituanWmHandler.showMessage)}

            🔜${buildMsgMenuUrl(ElmeWmHandler.reply, ElmeWmHandler.showMessage)}

            -----------------------
            【点击菜单栏，每天领最新红包】
        """.trimIndent()
        return buildText(content, wxMpXmlMessage)
    }

}