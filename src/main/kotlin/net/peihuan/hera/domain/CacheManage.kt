package net.peihuan.hera.domain

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import mu.KotlinLogging
import net.peihuan.hera.config.HeraProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.service.ConfigService
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CacheManage(
    private val configService: ConfigService,
    private val heraProperties: HeraProperties
) {

    private val log = KotlinLogging.logger {}

    val cache: Cache<BizConfigEnum, String> = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(heraProperties.cacheExpireSeconds, TimeUnit.SECONDS)
        .build()

    fun getBizValue(config: BizConfigEnum, default: String? = null): String {
        return cache.get(config) {
            val configValue: String? = configService.getConfigValue(config)
            log.info { "重新从数据库中读取业务配置 config: ${config.key}  value: $configValue" }
            configValue ?: default
        }
    }

    fun updateBizValue(config: BizConfigEnum, value: String) {
        configService.updateConfig(config.key, value)
    }

}