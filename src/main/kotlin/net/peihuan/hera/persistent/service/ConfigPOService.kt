package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.ConfigMapper
import net.peihuan.hera.persistent.po.ConfigPO
import org.springframework.stereotype.Service

@Service
class ConfigPOService : ServiceImpl<ConfigMapper, ConfigPO>() {

    fun getByKey(key: String): ConfigPO? {
        val config = getOne(KtQueryWrapper(ConfigPO::class.java).eq(ConfigPO::key, key)) ?: return null
        return removeExpired(config)
    }

    fun getByAppidKey(appid: String, key: String): ConfigPO? {
        val config = getOne(KtQueryWrapper(ConfigPO::class.java)
                .eq(ConfigPO::key, key)
                .eq(ConfigPO::appid, appid)) ?: return null
        return removeExpired(config)
    }

    fun listByKey(key: String): List<ConfigPO> {
        val list = list(KtQueryWrapper(ConfigPO::class.java).eq(ConfigPO::key, key))
        return removeExpired(list)
    }


    fun removeExpired(config: ConfigPO?): ConfigPO? {
        if (config == null) {
            return null
        }
        if (config.isExpired()) {
            removeById(config.id)
            return null
        }
        return config
    }


    fun removeExpired(configs: List<ConfigPO>): List<ConfigPO> {
        val group = configs.groupBy { it.isExpired() }

        val expiredIds = group[true]?.map { it.id }
        if (!expiredIds.isNullOrEmpty()) {
            removeByIds(expiredIds)
        }
        return group[false] ?: list()
    }


}