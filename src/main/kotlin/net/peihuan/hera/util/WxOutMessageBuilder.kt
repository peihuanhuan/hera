package net.peihuan.hera.util

import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage








fun buildImage(mediaId: String?, wxMessage: WxMpXmlMessage): WxMpXmlOutMessage {
    return WxMpXmlOutMessage.IMAGE().mediaId(mediaId)
            .fromUser(wxMessage.toUser).toUser(wxMessage.fromUser)
            .build()
}


fun buildText(content: String?, wxMessage: WxMpXmlMessage): WxMpXmlOutMessage {
    return WxMpXmlOutMessage.TEXT().content(content)
            .fromUser(wxMessage.toUser).toUser(wxMessage.fromUser)
            .build()
}

fun buildSendToKf(wxMessage: WxMpXmlMessage, kfOpenid: String): WxMpXmlOutMessage {
    return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
            .fromUser(wxMessage.toUser)
            .toUser(wxMessage.fromUser)
            .build()
}


// fun buildTex(content: String?, wxMessage: WxMpXmlMessage): WxMpXmlOutMessage {
//     return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE().setCommon()
//             .fromUser(wxMessage.toUser).toUser(wxMessage.fromUser)
//             .build()
// }