package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.UserTagMapper
import net.peihuan.hera.persistent.po.UserTagPO
import org.springframework.stereotype.Service

@Service
class UserTagPOService : ServiceImpl<UserTagMapper, UserTagPO>() {


    fun addUserTag(openid: String, tagid: Long) {
        val one = getOne(KtQueryWrapper(UserTagPO::class.java)
                .eq(UserTagPO::openid, openid)
                .eq(UserTagPO::tagid, tagid))
        if(one != null) {
            return
        }
        val po = UserTagPO(openid = openid, tagid = tagid)
        save(po)
    }
}