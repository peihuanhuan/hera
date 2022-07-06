package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.persistent.service.BilibiliAudioTaskPOService
import net.peihuan.hera.service.BlackKeywordService
import net.peihuan.hera.util.completeALable
import net.peihuan.hera.util.completeMsgMenu
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class BilibiliAudioProcessHandler(
    val bilibiliAudioTaskPOService: BilibiliAudioTaskPOService,
    val blackKeywordService: BlackKeywordService,
    val bilibiliAudioQunHandler: BilibiliAudioQunHandler,
) : AbstractMessageHandler {

    override fun receivedMessages(): List<String> {
        return listOf("进度", "进展")
    }

    override fun canHandle(message: String): Boolean {
        return message.contains("进度") || message.contains("进展")
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {

        val lastTask =
            bilibiliAudioTaskPOService.findLastByOpenid(wxMpXmlMessage.fromUser)
        if (lastTask == null) {
            val content = """
                |一个任务都没收到呢？？？
                | 
                |<a>阿烫的粉丝群</a>，想及时了解此工具最新动态，或无法导出音频求助，请进群，回复及时！
                |
                |<a>Bilibili 音频提取工具</a>
            """.trimMargin()
                .completeMsgMenu(bilibiliAudioQunHandler.receivedMessages().first())
                .completeALable("http://wx.peihuan.net/bilibili-audio/")
            wxMpXmlMessage.replyKfMessage(content)
            return null
        }
        val task = bilibiliAudioTaskPOService.getTask(lastTask.id!!)
        wxMpXmlMessage.replyKfMessage(task.getProcess())
        return null
    }

}