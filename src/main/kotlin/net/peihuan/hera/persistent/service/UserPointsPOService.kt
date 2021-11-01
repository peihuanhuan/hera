package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.UserPointsMapper
import net.peihuan.hera.persistent.po.UserPointsPO
import org.springframework.stereotype.Service

@Service
class UserPointsPOService : ServiceImpl<UserPointsMapper, UserPointsPO>() {

    fun getByOpenid(openid: String): UserPointsPO? {
        return getOne(KtQueryWrapper(UserPointsPO::class.java).eq(UserPointsPO::openid, openid))
    }
}