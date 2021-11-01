package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.UserMapper
import net.peihuan.hera.persistent.po.UserPO
import org.springframework.stereotype.Service

@Service
class UserPOService : ServiceImpl<UserMapper, UserPO>() {


    fun getByOpenid(openid: String): UserPO? {
        return getOne(KtQueryWrapper(UserPO::class.java).eq(UserPO::openid, openid))
    }
}