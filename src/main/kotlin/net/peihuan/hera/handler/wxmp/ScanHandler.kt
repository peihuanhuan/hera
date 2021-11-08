package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.service.ScanService
import org.springframework.stereotype.Component


@Component
class ScanHandler(private val scanService: ScanService) : AbstractHandler() {

    override fun handle(wxMpXmlMessage: WxMpXmlMessage, map: Map<String, Any>,
                        wxMpService: WxMpService, wxSessionManager: WxSessionManager): WxMpXmlOutMessage? {
        // 扫码事件处理
        scanService.handleQrsceneScan(wxMpXmlMessage, wxMpXmlMessage.eventKey)
        return null
    }
}