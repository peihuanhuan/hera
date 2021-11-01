package net.peihuan.hera.util

import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage




fun buildKfText(wxMessage: WxMpXmlMessage, content: String): WxMpKefuMessage {
    return buildKfText(wxMessage.fromUser, content)
}

fun buildKfText(openid: String, content: String): WxMpKefuMessage {
    return WxMpKefuMessage
            .TEXT()
            .toUser(openid)
            .content(content)
            .build()
}

fun buildKfImage(wxMessage: WxMpXmlMessage, mediaId: String): WxMpKefuMessage {
    return WxMpKefuMessage
            .IMAGE()
            .toUser(wxMessage.fromUser)
            .mediaId(mediaId)
            .build()
}


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


// fun buildTex(content: String?, wxMessage: WxMpXmlMessage): WxMpXmlOutMessage {
//     return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE().setCommon()
//             .fromUser(wxMessage.toUser).toUser(wxMessage.fromUser)
//             .build()
// }