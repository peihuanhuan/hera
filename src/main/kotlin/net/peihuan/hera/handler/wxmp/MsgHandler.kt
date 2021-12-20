package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.common.error.WxErrorException
import me.chanjar.weixin.common.session.WxSessionManager
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.HeraProperties
import net.peihuan.hera.handler.click.AbstractMessageHandler
import net.peihuan.hera.service.NotifyService
import net.peihuan.hera.util.buildSendToKf
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component


@Component
class MsgHandler(val messageHandlers: List<AbstractMessageHandler>,
                 val wxMpService: WxMpService,
                 val notifyService: NotifyService,
                 val heraProperties: HeraProperties
                ) : AbstractHandler() {
    override fun handle(wxMessage: WxMpXmlMessage,
                        context: Map<String, Any>, weixinService: WxMpService,
                        sessionManager: WxSessionManager): WxMpXmlOutMessage? {
        if (wxMessage.msgType != WxConsts.XmlMsgType.TEXT) {
            if (wxMessage.msgType == WxConsts.XmlMsgType.EVENT) {
                notifyService.notifyLeaveMessage(wxMessage.fromUser, "事件：${wxMessage.event} 状态：${wxMessage.status}")
            } else {
                notifyService.notifyLeaveMessage(wxMessage.fromUser, wxMessage.content)
            }
            return buildSendToKf(wxMessage, heraProperties.adminOpenid)
        }

        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        try {
            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
                    && weixinService.getKefuService().kfOnlineList()
                            .getKfOnlineList().size > 0) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser()).build()
            }
        } catch (e: WxErrorException) {
            e.printStackTrace()
        }

        messageHandlers.forEach {
            if (it.receivedMessages().contains(wxMessage.content)) {
                return it.handle(wxMessage)
            }
        }

        notifyService.notifyLeaveMessage(wxMessage.fromUser, wxMessage.content)
        return buildSendToKf(wxMessage, heraProperties.adminOpenid)
    }
}