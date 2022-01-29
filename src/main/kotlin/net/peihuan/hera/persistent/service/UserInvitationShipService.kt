package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder
import net.peihuan.hera.domain.InviterUser
import net.peihuan.hera.persistent.mapper.UserInvitationShipMapper
import net.peihuan.hera.persistent.po.UserInvitationShipPO
import org.springframework.stereotype.Service

@Service
class UserInvitationShipService : ServiceImpl<UserInvitationShipMapper, UserInvitationShipPO>() {


    fun addInvite(openid: String, inviterUser: InviterUser) {
        val appid = WxMpConfigStorageHolder.get()
        val userInvitationShipPO = UserInvitationShipPO(
            openid = openid,
            inviter = inviterUser.openid,
            channel = inviterUser.activityId,
            desc = "活动邀请",
            appid = appid
        )
        save(userInvitationShipPO)
    }


    fun findInviteUsers(openid: String): List<UserInvitationShipPO> {
        return list(KtQueryWrapper(UserInvitationShipPO::class.java).eq(UserInvitationShipPO::inviter, openid))
            // 防止一个人多次邀请一个人的错误
            // .distinctBy { it.openid }
    }

}