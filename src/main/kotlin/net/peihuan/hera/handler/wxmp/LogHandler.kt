package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.util.toJson
import org.springframework.stereotype.Component


@Component
class LogHandler : AbstractHandler() {
    override fun handle(wxMessage: WxMpXmlMessage,
                        context: Map<String, Any>, wxMpService: WxMpService,
                        sessionManager: WxSessionManager): WxMpXmlOutMessage? {
        log.info("\n接收到请求消息，内容：{}", wxMessage.toJson());
        return null
    }
}