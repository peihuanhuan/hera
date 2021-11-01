// package net.peihuan.hera.handler.message
//
// import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
// import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
// import net.peihuan.hera.service.SignService
// import net.peihuan.hera.util.buildText
// import org.springframework.stereotype.Component
//
// @Component
// class SignMessageHandler(val signService: SignService) : AbstractMessageHandler() {
//
//     override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
//         return buildText(signService.sign(wxMpXmlMessage.fromUser), wxMpXmlMessage)
//     }
//
//     override fun canHandle(message: String): Boolean {
//         return message == "签到"
//     }
// }