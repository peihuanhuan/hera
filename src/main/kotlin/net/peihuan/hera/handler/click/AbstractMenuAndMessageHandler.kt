package net.peihuan.hera.handler.click

abstract class AbstractMenuAndMessageHandler :AbstractMenuHandler, AbstractMessageHandler {
    // abstract override fun receivedMessages(): List<String>

    // abstract override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage?


    // 要保证唯一性
    // protected open fun reply(): String {
    //     return showMsg()
    // }

    // abstract override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        // return buildText(buildMsgMenuUrl(receivedMessages().first(), showMsg()), wxMpXmlMessage)
    // }


    // fun canHandleMessage(key: String?): Boolean {
    //     return key == reply()
    // }


}