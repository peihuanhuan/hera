package net.peihuan.hera.config

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import net.peihuan.hera.persistent.service.LockPOService
import net.peihuan.hera.service.ConfigService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(WxMpProperties::class)
class WxMpServiceConfiguration(
        val properties: WxMpProperties,
        val configService: ConfigService,
        val lockPOService: LockPOService
) {

    @Bean
    fun wxMpService(): WxMpService {
        val service: WxMpService = WxMpServiceImpl()
        val wxMysqlOps = WxMysqlOps(configService, lockPOService)
        val configStorages = properties.configs.map { mpConfig ->
            val configStorage = WxMpMysqlConfigImpl(wxMysqlOps, mpConfig.appId)
//            val configStorage = WxMpDefaultConfigImpl()
            configStorage.appId = mpConfig.appId.trim()
            configStorage.secret = mpConfig.secret.trim()
            configStorage.token = mpConfig.token.trim()
            configStorage.aesKey = mpConfig.aesKey.trim()
            // configStorage.accessToken =
            return@map configStorage
        }

        service.setMultiConfigStorages(configStorages.associateBy { it.appId })
        return service
    }

}