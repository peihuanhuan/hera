package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder
import net.peihuan.hera.domain.ActivityUser
import net.peihuan.hera.persistent.mapper.UserInvitationShipMapper
import net.peihuan.hera.persistent.po.UserInvitationShipPO
import org.springframework.stereotype.Service

@Service
class UserInvitationShipService : ServiceImpl<UserInvitationShipMapper, UserInvitationShipPO>() {


    fun addInvite(openid: String, activityUser: ActivityUser) {
        val appid = WxMpConfigStorageHolder.get()
        val userInvitationShipPO = UserInvitationShipPO(
            openid = openid,
            inviter = activityUser.openidId,
            channel = activityUser.activityId,
            appid = appid
        )
        save(userInvitationShipPO)
    }


    fun findInviteUsers(openid: String): List<UserInvitationShipPO> {
        return list(KtQueryWrapper(UserInvitationShipPO::class.java).eq(UserInvitationShipPO::inviter, openid))
            // 防止一个人多次邀请一个人的错误
            .distinctBy { it.openid }
    }

}