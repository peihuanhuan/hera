package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import me.chanjar.weixin.common.error.WxErrorException
import kotlin.Throws
import org.springframework.stereotype.Component


@Component
class ScanHandler : AbstractHandler() {
    @Throws(WxErrorException::class)
    override fun handle(wxMpXmlMessage: WxMpXmlMessage, map: Map<String, Any>,
                        wxMpService: WxMpService, wxSessionManager: WxSessionManager): WxMpXmlOutMessage? {
        // 扫码事件处理
        return null
    }
}