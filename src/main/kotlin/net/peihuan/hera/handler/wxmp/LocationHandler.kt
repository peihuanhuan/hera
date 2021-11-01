package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.util.buildText
import org.springframework.stereotype.Component

@Component
class LocationHandler : AbstractHandler() {
    override fun handle(wxMessage: WxMpXmlMessage,
                        context: Map<String, Any>, wxMpService: WxMpService,
                        sessionManager: WxSessionManager): WxMpXmlOutMessage? {
        if (wxMessage.getMsgType() == WxConsts.XmlMsgType.LOCATION) {
            //TODO 接收处理用户发送的地理位置消息
            return try {
                val content = "感谢反馈，您的的地理位置已收到！"
                buildText(content, wxMessage)
            } catch (e: Exception) {
                log.error("位置消息接收处理失败", e)
                return null
            }
        }

        //上报地理位置事件
        log.info("上报地理位置，纬度 : {}，经度 : {}，精度 : {}",
                wxMessage.getLatitude(), wxMessage.getLongitude(), wxMessage.getPrecision().toString())

        //TODO  可以将用户地理位置信息保存到本地数据库，以便以后使用
        return null
    }
}