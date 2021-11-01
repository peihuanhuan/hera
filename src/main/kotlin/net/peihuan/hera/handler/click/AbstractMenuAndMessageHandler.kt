package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.util.buildMsgMenuUrl
import net.peihuan.hera.util.buildText

abstract class AbstractMenuAndMessageHandler :AbstractMenuHandler() {

    abstract fun showMsg(): String

    // 要保证唯一性
    protected open fun reply(): String {
        return showMsg()
    }


    override fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        return buildText(buildMsgMenuUrl(reply(), showMsg()), wxMpXmlMessage)
    }


    fun canHandleMessage(key: String): Boolean {
        return key == reply()
    }

    abstract fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage?

}