package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.HeraProperties
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.service.ChannelService
import net.peihuan.hera.util.ZyUtil
import net.peihuan.hera.util.buildALabel
import net.peihuan.hera.util.replyKfImage
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class ExchangeMemberMessageHandler(private val wxMpService: WxMpService,
                                   private val zyProperties: ZyProperties,
                                   private val cacheManage: CacheManage,
                                   private val channelService: ChannelService,
                                   private val heraProperties: HeraProperties) : AbstractMenuAndMessageHandler() {

    companion object {
        const val receivedMessage = "兑换会员"
    }

    override fun receivedMessages(): List<String> {
        return listOf(receivedMessage)
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "exchangeMember"
    }


    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        sendMessage(wxMpXmlMessage)
        return null
    }

    private fun sendMessage(wxMpXmlMessage: WxMpXmlMessage) {

        val channelId = channelService.getChannelOrCreate(wxMpXmlMessage.fromUser).id
        val productUrl = ZyUtil.buildAllProductUrl(channelId, zyProperties.appid)

        val aLabel = buildALabel(productUrl, "➜ 戳我 六十余种超低会员，应有尽有！")
        val content = """
                小主，可以兑换这里的所有会员哦
                
                $aLabel
                
                每 100 积分价值 1 元，请扫码添加客服进行兑换会员
                
                每半个月只能兑换一次~
            """.trimIndent()

        wxMpXmlMessage.replyKfMessage(content)

        val kefuMedia = cacheManage.getBizValue(BizConfigEnum.MEDIA_KEFU)
        wxMpXmlMessage.replyKfImage(kefuMedia)
    }


}