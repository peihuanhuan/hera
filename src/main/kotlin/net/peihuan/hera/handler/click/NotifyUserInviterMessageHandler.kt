package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.persistent.service.UserInvitationShipService
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class NotifyUserInviterMessageHandler(val userInvitationShipService: UserInvitationShipService) : AbstractMenuAndMessageHandler() {

    companion object {
        const val reply = "问下我邀请多少个好友了呢？"
    }

    override fun showMsg(): String {
        return "use less"
    }

    override fun reply(): String {
        return reply
    }

    override fun canHandleMenuClick(key: String): Boolean {
        return key == "xxxx"
    }

    override fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        return null
    }

    override fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val findInviteUsers = userInvitationShipService.findInviteUsers(wxMpXmlMessage.fromUser)
        if (findInviteUsers.isEmpty()) {
            wxMpXmlMessage.replyKfMessage("您一个人都还没有邀请哦!")
            return null
        }

        wxMpXmlMessage.replyKfMessage("您已经邀请 ${findInviteUsers.size} 个好友啦！")
        return null
    }

}