package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.constants.StatusEnum
import net.peihuan.hera.persistent.mapper.SubscribeMapper
import net.peihuan.hera.persistent.po.SubscribePO
import org.springframework.stereotype.Service

@Service
class SubscribePOService : ServiceImpl<SubscribeMapper, SubscribePO>() {

    fun getSubscribeOpenids() : List<String> {
        return getBaseMapper().findSubscribeOpenids()
    }

    fun getLastOnSubscribe(openid: String): SubscribePO? {
        return getOne(KtQueryWrapper(SubscribePO::class.java)
            .eq(SubscribePO::openid, openid)
            .eq(SubscribePO::status, StatusEnum.ON.code)
            .orderByDesc(SubscribePO::createTime)
            .last("limit 1"))
    }

    fun getSubscribes(openid: String): List<SubscribePO> {
        return list(KtQueryWrapper(SubscribePO::class.java)
            .eq(SubscribePO::openid, openid)
            .orderByDesc(SubscribePO::createTime))
    }

    fun unSubscribeRecord(openid: String) {
        val userSubscribe = getLastOnSubscribe(openid) ?: return
        userSubscribe.status = StatusEnum.OFF.code
        userSubscribe.updateTime = null
        updateById(userSubscribe)
    }

}