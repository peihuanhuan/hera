package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.util.ZyUtil
import net.peihuan.hera.util.buildALabel
import net.peihuan.hera.util.buildKfImage
import net.peihuan.hera.util.buildKfText
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ExchangeMemberMessageHandler(val wxMpService: WxMpService,
                                   val zyProperties: ZyProperties,
                                   @Value("\${media_id.wechat}") val wechatMediaId: String) : AbstractMenuAndMessageHandler() {

    companion object {
        const val receivedMessage = "兑换会员"
    }

    override fun showMsg(): String {
        return "useless"
    }

    override fun reply(): String {
        return receivedMessage
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "exchangeMember"
    }

    override fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        sendMessage(wxMpXmlMessage)
        return null
    }

    override fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        sendMessage(wxMpXmlMessage)
        return null
    }

    private fun sendMessage(wxMpXmlMessage: WxMpXmlMessage) {

        val productUrl = ZyUtil.buildAllProductUrl(wxMpXmlMessage.fromUser, zyProperties.appid)

        val aLabel = buildALabel(productUrl, "➜ 戳我 六十余种超低会员，应有尽有！")
        val content = """
                小主，可以兑换这里的所有会员哦
                
                $aLabel
                
                每 100 积分价值 1 元，请扫码添加客服进行兑换会员
                
                每半个月只能兑换一次~
            """.trimIndent()

        wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, content))
        wxMpService.kefuService.sendKefuMessage(buildKfImage(wxMpXmlMessage, wechatMediaId));
    }


}