package net.peihuan.hera.service.convert

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo
import me.chanjar.weixin.mp.bean.result.WxMpUser
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder
import net.peihuan.hera.persistent.po.UserPO
import net.peihuan.hera.util.toJson
import org.springframework.stereotype.Component

@Component
class UserConvertService {
    fun convertToUserPO(mpUser: WxMpUser): UserPO {
        // todo 多公众号
        val appid = WxMpConfigStorageHolder.get()
        return UserPO(
                nickname = mpUser.nickname,
                appid = appid,
                openid = mpUser.openId,
                unionid = mpUser.unionId,
                sex = mpUser.sex,
                province = mpUser.province,
                city = mpUser.city,
                country = mpUser.country,
                headimgurl = mpUser.headImgUrl,
                privilege = mpUser.privileges.toJson(),
                remark = mpUser.remark,
                groupid = mpUser.groupId,
                updateTime = null,
        )
        // TODO: 保存头像
        // todo 处理标签
    }


    fun convertToUserPO(mpUser: WxOAuth2UserInfo): UserPO {
        // todo 多公众号
        val appid = WxMpConfigStorageHolder.get()
        return UserPO(
            nickname = mpUser.nickname,
            appid = appid,
            openid = mpUser.openid,
            unionid = mpUser.unionId,
            sex = mpUser.sex,
            province = mpUser.province,
            city = mpUser.city,
            country = mpUser.country,
            headimgurl = mpUser.headImgUrl,
            privilege = mpUser.privileges.toJson(),
            remark = "",
            groupid = 0,
            updateTime = null,
        )
        // TODO: 保存头像
        // todo 处理标签
    }
}