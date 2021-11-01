package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.service.UserPointsService
import net.peihuan.hera.util.MIN_POINTS_CAN_EXCHANGE_MEMBER
import net.peihuan.hera.util.buildMsgMenuUrl
import net.peihuan.hera.util.buildText
import org.springframework.stereotype.Component

@Component
class PointsMessageAndMessageHandler(val userPointsService: UserPointsService) : AbstractMenuHandler() {

    override fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage {
        return getUserPointMessage(wxMpXmlMessage)
    }

    private fun getUserPointMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage {
        val userPoints = userPointsService.getUserPoints(wxMpXmlMessage.fromUser)
        if (userPoints == 0) {
            return buildText("现在还没有积分哦，努力签到吧！", wxMpXmlMessage)
        }

        val content = """
                现在已经有 ${userPoints} 积分啦
                
                只要 $MIN_POINTS_CAN_EXCHANGE_MEMBER 积分就可以兑换会员了
                
                ——————————————
                ${buildMsgMenuUrl(ExchangeMemberMessageHandler.receivedMessage, "➜ 戳我兑换会员")}
            """.trimIndent()

        return buildText(content, wxMpXmlMessage)
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "points"
    }

}