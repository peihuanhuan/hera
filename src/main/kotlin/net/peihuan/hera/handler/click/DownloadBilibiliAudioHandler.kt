package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.persistent.service.BilibiliAudioTaskPOService
import net.peihuan.hera.service.BlackKeywordService
import net.peihuan.hera.util.completeALable
import net.peihuan.hera.util.completeMsgMenu
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class DownloadBilibiliAudioHandler(
    val bilibiliAudioTaskPOService: BilibiliAudioTaskPOService,
    val blackKeywordService: BlackKeywordService,
    val bilibiliAudioQunHandler: BilibiliAudioQunHandler,
) : AbstractMessageHandler {

    override fun receivedMessages(): List<String> {
        return listOf("音频", "【音频】")
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {

        val successTasks =
            bilibiliAudioTaskPOService.findByOpenidAndStatus(wxMpXmlMessage.fromUser, TaskStatusEnum.SUCCESS)
        if (successTasks.isEmpty()) {
            val content = """
                |最近没有成功的任务呢？应该还在转换中吧？
                | 
                |<a>BiliBili玩梗闲聊群</a>，任务长时间没结果就找群主 _(:з」∠)_
                |
                |<a>Bilibili 音频提取</a>
            """.trimMargin()
                .completeMsgMenu(bilibiliAudioQunHandler.receivedMessages().first())
            wxMpXmlMessage.replyKfMessage(content)
            return null
        }
        var content = """上一个成功任务为：
                |${successTasks.first().name}
                |
                |<a>BiliBili玩梗闲聊群</a>，任务长时间没结果就找群主  _(:з」∠)_
                |
                |<a>Bilibili 音频提取工具</a>
            """.trimMargin()
            .completeMsgMenu(bilibiliAudioQunHandler.receivedMessages().first())
            .completeALable("http://wx.peihuan.net/bilibili-audio/")

        content = blackKeywordService.replaceBlackKeyword(content)

        wxMpXmlMessage.replyKfMessage(content)
        wxMpXmlMessage.replyKfMessage(successTasks.first().url)
        return null
    }

}