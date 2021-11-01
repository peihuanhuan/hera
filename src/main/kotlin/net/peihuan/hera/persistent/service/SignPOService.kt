package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.SignMapper
import net.peihuan.hera.persistent.po.SignPO
import org.springframework.stereotype.Service

@Service
class SignPOService : ServiceImpl<SignMapper, SignPO>() {


    fun getLastSign(openid: String): SignPO? {
        return getOne(KtQueryWrapper(SignPO::class.java)
                .eq(SignPO::openid, openid)
                .orderByDesc(SignPO::createTime)
                .last("limit 1"))
    }


}