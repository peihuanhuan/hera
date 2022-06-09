package net.peihuan.hera.handler.click

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.property.HeraProperties
import net.peihuan.hera.config.property.ZyProperties
import net.peihuan.hera.feign.service.FastposterService
import net.peihuan.hera.persistent.service.ActivityPOService
import net.peihuan.hera.persistent.service.UserPOService
import net.peihuan.hera.service.ChannelService
import net.peihuan.hera.util.*
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.util.*

@Component
class RedPackageMessageHandler(private val wxMpService: WxMpService,
                               private val userService: UserPOService,
                               private val zyProperties: ZyProperties,
                               private val channelService: ChannelService,
                               private val notifyUserInviterMessageHandler: NotifyUserInviterMessageHandler,
                               private val notifyUserMessageHandler: NotifyUserMessageHandler,
                               private val activityService: ActivityPOService,
                               private val fastposterService: FastposterService,
                               private val heraProperties: HeraProperties
) : AbstractMessageHandler {

    override fun receivedMessages(): List<String> {
        return listOf("封面")
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        sendMessage(wxMpXmlMessage.fromUser)
        return null
    }


    fun sendMessage(openid: String) {
        val content = """
            邀请好友免费领红包封面！！！
            
            通过海报邀请三位好友首次关注公众号，即可获得虎年封面红包
            
            🔜 <a>戳我生成专属分享海报！！！</a>
            """.trimIndent().completeALable("https://wx.peihuan.net/red/index.html")
        openid.replyKfMessage(content)
    }



    fun process(openid: String) {
        val activityPO = activityService.getByKeyword(receivedMessages().first())
        if (activityPO == null || activityPO.disable) {
            openid.replyKfMessage("抱歉，活动已经下线了哦~")
            return
        }
        if (activityPO.startTime.after(Date())) {
            openid.replyKfMessage("活动还没有开始哦，请再等等吧~")
            return
        }
        if (activityPO.endTime.before(Date())) {
            openid.replyKfMessage("活动已经结束了哦，下次记得早点来吧~")
            return
        }

        val encodeInviterQrscene = encodeInviter(activityPO.id, openid)

        val qrCodeCreateLastTicket = wxMpService.qrcodeService.qrCodeCreateTmpTicket(encodeInviterQrscene, 2592000)

        val simpleUser = userService.getByOpenid(openid) ?: return
        val response = fastposterService.generatePoster(
            FastposterService.Request(
                "1",
                qrCodeCreateLastTicket.url,
                simpleUser.headimgurl!!,
                simpleUser.nickname!!
            )
        )

        val tempFile = downloadPosterFile(response)

        val mediaUpload = wxMpService.materialService.mediaUpload(WxConsts.MaterialType.IMAGE, tempFile)


        openid.replyKfImage(mediaUpload.mediaId)

        val content = """
                专属海报已送达！
                
                <a>拍一拍小助理的脑袋，提醒我邀请进度</a>
                
                <a>【戳我一下】</a>，可以看看已经邀请多少人了呢
                """.trimIndent()
        openid.replyKfMessage(
            content.completeMsgMenu(
                notifyUserMessageHandler.receivedMessages().first(),
                notifyUserInviterMessageHandler.receivedMessages().first()
            )
        )

        FileUtils.deleteQuietly(tempFile)
    }

    private fun downloadPosterFile(response: FastposterService.Response): File {
        val tempFile = File.createTempFile("tmp-", ".jpg")
        FileUtils.copyURLToFile(URL(response.url), tempFile)
        return tempFile
    }


}