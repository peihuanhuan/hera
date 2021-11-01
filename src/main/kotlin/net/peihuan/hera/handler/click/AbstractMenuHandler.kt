package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage

abstract class AbstractMenuHandler {

    abstract fun canHandleMenuClick(key: String): Boolean

    abstract fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage?
}