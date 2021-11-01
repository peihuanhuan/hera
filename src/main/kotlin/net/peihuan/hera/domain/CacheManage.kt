package net.peihuan.hera.domain

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import mu.KotlinLogging
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.service.ConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CacheManage(private val configService: ConfigService,
                  @Value("\${cache.expire_seconds}") val expireSeconds: Long) {

    private val log = KotlinLogging.logger {}

    val cache: Cache<BizConfigEnum, String> = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
            .build()

    fun getBizValue(config: BizConfigEnum): String {
        return cache.get(config) {
            val configValue: String? = configService.getConfigValue(config)
            log.info { "重新从数据库中读取业务配置 config: ${config.key}  value: $configValue" }
            configValue!!
        }
    }

}