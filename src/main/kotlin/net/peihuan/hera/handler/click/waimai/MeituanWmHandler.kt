package net.peihuan.hera.handler.click.waimai

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.handler.click.AbstractMenuAndMessageHandler
import net.peihuan.hera.util.buildMsgMenuUrl
import net.peihuan.hera.util.buildText
import org.springframework.stereotype.Component


@Component
class MeituanWmHandler(
        private val zyProperties: ZyProperties,
        private val cacheManage: CacheManage,
) : AbstractMenuAndMessageHandler() {
    companion object {
        const val reply = "【点我】美团红包"
        const val showMessage = "【点我】美团外卖大额红包"
    }

    override fun showMsg(): String {
        return showMessage
    }

    override fun reply(): String {
        return reply
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "meituan"
    }

    override fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage {
        var meituanContent = cacheManage.getBizValue(BizConfigEnum.MEITUAN)
        meituanContent += buildMsgMenuUrl(ElmeWmHandler.reply, "还有饿了么红包未领取哦~")
        return buildText(meituanContent, wxMpXmlMessage)
    }
}