package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.persistent.service.BilibiliAudioTaskPOService
import net.peihuan.hera.service.BlackKeywordService
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class AutoReplyHandler(
    val bilibiliAudioTaskPOService: BilibiliAudioTaskPOService,
    val blackKeywordService: BlackKeywordService,
    val cacheManage: CacheManage,
    val bilibiliAudioQunHandler: BilibiliAudioQunHandler,
) : AbstractMessageHandler {

    override fun receivedMessages(): List<String> {
        val bizValueList = cacheManage.getBizValueList(BizConfigEnum.AUTO_REPLY)
        val allow = mutableListOf<String>()
        bizValueList.map {
            it.split("|")[0].split(",").forEach { q->
                allow.add(q.trim())
            }
        }
        return allow
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {

        val bizValueList = cacheManage.getBizValueList(BizConfigEnum.AUTO_REPLY)
        bizValueList.forEach {
            val split = it.split("|")
            split[0].trim().split(",").forEach { q ->
                if (q == wxMpXmlMessage.content) {
                    wxMpXmlMessage.replyKfMessage(split[1].trim())
                    return null
                }
            }
        }

        return null
    }

}