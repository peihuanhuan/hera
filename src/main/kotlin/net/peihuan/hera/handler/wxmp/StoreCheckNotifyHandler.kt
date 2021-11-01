package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import org.springframework.stereotype.Component

/**
 * 门店审核事件处理
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
class StoreCheckNotifyHandler : AbstractHandler() {
    override fun handle(wxMessage: WxMpXmlMessage,
                        context: Map<String, Any>, wxMpService: WxMpService,
                        sessionManager: WxSessionManager): WxMpXmlOutMessage? {
        // TODO 处理门店审核事件
        return null
    }
}