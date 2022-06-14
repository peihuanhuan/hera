package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.UserConfigMapper
import net.peihuan.hera.persistent.po.UserConfigPO
import org.springframework.stereotype.Service

@Service
class UserConfigPOService : ServiceImpl<UserConfigMapper, UserConfigPO>() {


    fun getUserConfigByKey(openid: String, key: String): UserConfigPO? {
        val config = getOne(KtQueryWrapper(UserConfigPO::class.java)
            .eq(UserConfigPO::openid, openid)
            .eq(UserConfigPO::key, key)) ?: return null
        return removeExpired(config)
    }



    fun removeExpired(config: UserConfigPO?): UserConfigPO? {
        if (config == null) {
            return null
        }
        if (config.isExpired()) {
            removeById(config.id)
            return null
        }
        return config
    }


    fun removeExpired(configs: List<UserConfigPO>): List<UserConfigPO> {
        val group = configs.groupBy { it.isExpired() }

        val expiredIds = group[true]?.map { it.id }
        if (!expiredIds.isNullOrEmpty()) {
            removeByIds(expiredIds)
        }
        return group[false] ?: list()
    }


}