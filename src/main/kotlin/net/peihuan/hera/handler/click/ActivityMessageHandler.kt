package net.peihuan.hera.handler.click

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.HeraProperties
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.feign.service.FastposterService
import net.peihuan.hera.persistent.service.ActivityPOService
import net.peihuan.hera.service.ChannelService
import net.peihuan.hera.util.completeMsgMenu
import net.peihuan.hera.util.encodeInviter
import net.peihuan.hera.util.replyKfImage
import net.peihuan.hera.util.replyKfMessage
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.util.*

@Component
class ActivityMessageHandler(private val wxMpService: WxMpService,
                             private val zyProperties: ZyProperties,
                             private val channelService: ChannelService,
                             private val notifyUserInviterMessageHandler: NotifyUserInviterMessageHandler,
                             private val notifyUserMessageHandler: NotifyUserMessageHandler,
                             private val activityService: ActivityPOService,
                             private val fastposterService: FastposterService,
                             private val heraProperties: HeraProperties) : AbstractMessageHandler {

    override fun receivedMessages(): List<String> {
        return listOf("海报")
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        sendMessage(wxMpXmlMessage)
        return null
    }

    private fun sendMessage(wxMpXmlMessage: WxMpXmlMessage) {
        val content1 = """
            邀请好友免费送会员！！！
            
            通过海报邀请三位好友首次关注公众号，即可获得海报中任意一个会员周卡（兑换请联系客服）
            
            🔜 快马加鞭生成专属海报中，请稍等片刻~
            """.trimIndent()
        wxMpXmlMessage.replyKfMessage(content1)

        val activityPO = activityService.getByKeyword(receivedMessages().first())
        if (activityPO == null || activityPO.disable) {
            wxMpXmlMessage.replyKfMessage("抱歉，活动已经下线了哦~")
            return
        }
        if (activityPO.startTime.after(Date())) {
            wxMpXmlMessage.replyKfMessage("活动还没有开始哦，请再等等吧~")
            return
        }
        if (activityPO.endTime.before(Date())) {
            wxMpXmlMessage.replyKfMessage("活动已经结束了哦，下次记得早点来吧~")
            return
        }

        val encodeInviterQrscene = encodeInviter(activityPO.id, wxMpXmlMessage.fromUser)

        val qrCodeCreateLastTicket = wxMpService.qrcodeService.qrCodeCreateTmpTicket(encodeInviterQrscene, 2592000)

        val response = fastposterService.generatePoster(FastposterService.Request("4", qrCodeCreateLastTicket.url))

        val tempFile = downloadPosterFile(response)

        val mediaUpload = wxMpService.materialService.mediaUpload(WxConsts.MaterialType.IMAGE, tempFile)


        wxMpXmlMessage.replyKfImage(mediaUpload.mediaId)

        val content = """
            专属海报已送达！
            
            <a>拍一拍小助理的脑袋，提醒我邀请进度</a>
            
            <a>【戳我一下】</a>，可以看看已经邀请多少人了呢，如果目标达成，快去添加客服微信吧~
            
            （点击底部菜单栏，积分——积分兑换）
            """.trimIndent()
        wxMpXmlMessage.replyKfMessage(content.completeMsgMenu(
            notifyUserMessageHandler.receivedMessages().first(),
            notifyUserInviterMessageHandler.receivedMessages().first()))

        FileUtils.deleteQuietly(tempFile)
    }

    private fun downloadPosterFile(response: FastposterService.Response): File {
        val tempFile = File.createTempFile("tmp-", ".jpg")
        FileUtils.copyURLToFile(URL(response.url), tempFile)
        return tempFile
    }


}