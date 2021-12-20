package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage

interface AbstractMessageHandler {

    fun receivedMessages(): List<String>

    fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage?
}