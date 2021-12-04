package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.UserMapper
import net.peihuan.hera.persistent.po.UserPO
import org.springframework.stereotype.Service

@Service
class UserPOService : ServiceImpl<UserMapper, UserPO>() {


    fun getByOpenid(openid: String): UserPO? {
        return getOne(KtQueryWrapper(UserPO::class.java).eq(UserPO::openid, openid))
    }


    fun pageUsers(nickname: String?, pageDTO: PageDTO<UserPO>): Page<UserPO> {
        return page(
            pageDTO,
            KtQueryWrapper(UserPO::class.java)
                .like(nickname != null, UserPO::nickname, nickname)
        )
    }

}