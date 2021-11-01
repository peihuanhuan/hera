package net.peihuan.hera.handler.message

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage

abstract class AbstractMessageHandler {
    abstract fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage?

    abstract fun canHandle(message: String): Boolean
}