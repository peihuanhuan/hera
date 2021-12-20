package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.handler.click.AbstractMenuHandler
import net.peihuan.hera.util.buildText
import org.springframework.stereotype.Component


@Component
class MenuHandler(val clickEventsHandlers: List<AbstractMenuHandler>) : AbstractHandler() {

    override fun handle(wxMessage: WxMpXmlMessage,
                        context: Map<String, Any>, weixinService: WxMpService,
                        sessionManager: WxSessionManager): WxMpXmlOutMessage? {
        if (WxConsts.EventType.VIEW == wxMessage.event) {
            return null
        }

        clickEventsHandlers.forEach {
            if (it.canHandleMenuClick(wxMessage.eventKey)) {
                log.info { "用户 ${wxMessage.fromUser} 点击了菜单 ${wxMessage.eventKey}" }
                return it.handle(wxMessage)
            }
        }

        return buildText("没找到该事件", wxMessage)
    }
}