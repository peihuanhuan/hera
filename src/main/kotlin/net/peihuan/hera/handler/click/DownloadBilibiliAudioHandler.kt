package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.persistent.service.BilibiliAudioTaskPOService
import net.peihuan.hera.util.completeALable
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class DownloadBilibiliAudioHandler(val bilibiliAudioTaskPOService: BilibiliAudioTaskPOService) : AbstractMessageHandler {

    override fun receivedMessages(): List<String> {
        return listOf("音频", "【音频】")
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {

        val successTasks = bilibiliAudioTaskPOService.findByOpenid(wxMpXmlMessage.fromUser)
        if (successTasks.isEmpty()) {
            wxMpXmlMessage.replyKfMessage("最近没有成功的任务呢？")
            return null
        }
        val content = """上一个成功任务为：
                |${successTasks.first().name}
                |
                |请复制以下链接到「系统浏览器」中下载，微信中无法下载。
                |
                |<a>Bilibili 音频提取</a>
            """.trimMargin().completeALable("http://wx.peihuan.net/bilibili-audio/")
        wxMpXmlMessage.replyKfMessage(
            content
        )
        wxMpXmlMessage.replyKfMessage(successTasks.first().url)
        return null
    }

}