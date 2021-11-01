package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.service.UserService
import org.springframework.stereotype.Component


@Component
class UnsubscribeHandler(val userService: UserService) : AbstractHandler() {
    override fun handle(wxMessage: WxMpXmlMessage,
                        context: Map<String, Any>, wxMpService: WxMpService,
                        sessionManager: WxSessionManager,
    ): WxMpXmlOutMessage? {
        val openId = wxMessage.fromUser
        log.info("取消关注用户 OPENID: $openId")
        userService.userUnSubscribeEvent(openId)
        return null
    }
}