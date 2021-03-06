package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage

interface AbstractMenuHandler {

    fun canHandleMenuClick(key: String): Boolean

    fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage?
}