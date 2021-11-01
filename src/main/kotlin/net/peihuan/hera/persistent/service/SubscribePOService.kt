package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.constants.StatusEnum
import net.peihuan.hera.persistent.mapper.SubscribeMapper
import net.peihuan.hera.persistent.po.SubscribePO
import org.springframework.stereotype.Service

@Service
class SubscribePOService : ServiceImpl<SubscribeMapper, SubscribePO>() {
    fun getUserSubscribe(openid: String): SubscribePO? {
        return getOne(KtQueryWrapper(SubscribePO::class.java)
                .eq(SubscribePO::openid, openid)
                .eq(SubscribePO::status, StatusEnum.ON.code)
                .orderByDesc(SubscribePO::createTime)
                .last("limit 1"))
    }

    fun unSubscribeRecord(openid: String) {
        val userSubscribe = getUserSubscribe(openid) ?: return
        userSubscribe.status = StatusEnum.OFF.code
        updateById(userSubscribe)
    }

}