package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.common.error.WxErrorException
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.service.UserService
import org.springframework.stereotype.Component


@Component
class SubscribeHandler(val userService: UserService) : AbstractHandler() {

    @Throws(WxErrorException::class)
    override fun handle(wxMessage: WxMpXmlMessage,
                        context: Map<String, Any>, weixinService: WxMpService,
                        sessionManager: WxSessionManager): WxMpXmlOutMessage? {
        log.info("新关注用户 OPENID: " + wxMessage.fromUser)
        return userService.userSubscribeEvent(wxMessage)

    }
}