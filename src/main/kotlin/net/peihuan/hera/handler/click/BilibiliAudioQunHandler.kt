package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.util.replyKfImage
import org.springframework.stereotype.Component

@Component
class BilibiliAudioQunHandler(val cacheManage: CacheManage) : AbstractMessageHandler {

    override fun receivedMessages(): List<String> {
        return listOf("阿烫的粉丝群")
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val media = cacheManage.getBizValue(BizConfigEnum.BILIBILI_QUN)
        wxMpXmlMessage.replyKfImage(media)
        return null
    }

}