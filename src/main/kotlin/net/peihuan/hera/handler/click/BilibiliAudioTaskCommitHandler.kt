package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.constants.BilibiliTaskTypeEnum
import net.peihuan.hera.constants.NotifyTypeEnum
import net.peihuan.hera.service.BVideo2AudioService
import net.peihuan.hera.util.removeCurrentUser
import net.peihuan.hera.util.replyKfMessage
import net.peihuan.hera.util.setCurrentUser
import org.springframework.stereotype.Component

@Component
class BilibiliAudioTaskCommitHandler(val bVideo2AudioService: BVideo2AudioService) : AbstractMessageHandler {
    override fun receivedMessages(): List<String> {
        return emptyList()
    }

    override fun canHandle(message: String): Boolean {
        return message.contains("www.bilibili.com") || message.contains("b23.tv")
    }


    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        try {
            setCurrentUser(wxMpXmlMessage.fromUser)
            val cnt = bVideo2AudioService.saveTask2DB(
                wxMpXmlMessage.content,
                BilibiliTaskTypeEnum.FREE.code,
                NotifyTypeEnum.MP_REPLY
            )
            wxMpXmlMessage.replyKfMessage("解析到了 $cnt 个视频，请静候佳音。")
        } catch (e: Exception) {
            wxMpXmlMessage.replyKfMessage(e.message?: "处理失败")
        } finally {
            removeCurrentUser()
        }
        return null
    }

}