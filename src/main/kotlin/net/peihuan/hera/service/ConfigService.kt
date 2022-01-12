package net.peihuan.hera.service

import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.persistent.po.ConfigPO
import net.peihuan.hera.persistent.service.ConfigPOService
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class ConfigService(private val configPOService: ConfigPOService) {


    // 先查询专属配置，无则降级到通用配置
    fun getConfigWithCommon(appid: String, enum: BizConfigEnum): String? {
        val configValue = getConfigValue(appid, enum)
        if (configValue != null) {
            return configValue
        }
        return getConfigValue(enum)

    }

    // 查询专属配置
    fun getConfigValue(appid: String, enum: BizConfigEnum): String? {
        return configPOService.getByAppidKey(appid, enum.key)?.value
    }

    // 查询通用配置
    fun getConfigValue(enum: BizConfigEnum): String? {
        return configPOService.getByKey(enum.key)?.value
    }

    fun getByKey(key: String): ConfigPO? {
        return configPOService.getByKey(key)
    }

    fun updateConfig(key: String, value: String?, expire: Long, timeUnit: TimeUnit) {
        val expireAt = timeUnit.toMillis(expire) + System.currentTimeMillis()

        var config = configPOService.getByKey(key)
        if (config == null) {
            config = ConfigPO()
        }
        config.key = key
        config.value = value
        config.expireAt = Date(expireAt)
        configPOService.saveOrUpdate(config)
    }

    fun updateConfig(key: String, value: String) {
        val config = configPOService.getByKey(key) ?: return
        config.value = value
        config.updateTime = null
        configPOService.updateById(config)
    }


    fun expireKey(key: String) {
        val configs = configPOService.listByKey(key)
        if (configs.isEmpty()) {
            return
        }
        if (configs.size > 1) {
            throw BizException.buildBizException("同时失效的配置数不能大于 1")
        }
        configPOService.removeById(configs.first().id)
    }

}