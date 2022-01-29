package net.peihuan.hera.util

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import net.peihuan.hera.domain.SpringContextHolder


private val wxMpService = SpringContextHolder.getBean(WxMpService::class)

fun String.replyKfMessage(content: String) {
    wxMpService.kefuService.sendKefuMessage(buildKfText(this, content))
}


fun WxMpXmlMessage.replyKfMessage(content: String) {
    wxMpService.kefuService.sendKefuMessage(buildKfText(this, content))
}

fun String.replyKfImage(mediaId: String) {
    wxMpService.kefuService.sendKefuMessage(buildKfImage(this, mediaId))
}

fun WxMpXmlMessage.replyKfImage(mediaId: String) {
    wxMpService.kefuService.sendKefuMessage(buildKfImage(this.fromUser, mediaId))
}

private fun buildKfImage(openid: String, mediaId: String): WxMpKefuMessage {
    return WxMpKefuMessage
        .IMAGE()
        .toUser(openid)
        .mediaId(mediaId)
        .build()
}

private fun buildKfText(wxMessage: WxMpXmlMessage, content: String): WxMpKefuMessage {
    return buildKfText(wxMessage.fromUser, content)
}

private fun buildKfText(openid: String, content: String): WxMpKefuMessage {
    return WxMpKefuMessage
        .TEXT()
        .toUser(openid)
        .content(content)
        .build()
}