package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import org.springframework.stereotype.Component


@Component
class NullHandler : AbstractHandler() {
    override fun handle(wxMessage: WxMpXmlMessage,
                        context: Map<String, Any>, wxMpService: WxMpService,
                        sessionManager: WxSessionManager): WxMpXmlOutMessage? {
        return null
    }
}