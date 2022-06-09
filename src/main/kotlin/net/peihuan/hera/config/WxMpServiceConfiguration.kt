package net.peihuan.hera.config

import com.github.binarywang.wxpay.config.WxPayConfig
import com.github.binarywang.wxpay.service.WxPayService
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl
import net.peihuan.hera.config.property.WxPayProperties
import net.peihuan.hera.persistent.service.LockPOService
import net.peihuan.hera.service.ConfigService
import org.apache.commons.lang3.StringUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class WxMpServiceConfiguration(
    val mpProperties: WxMpProperties,
    val payProperties: WxPayProperties,
    val configService: ConfigService,
    val lockPOService: LockPOService
) {

    @Bean
    fun wxMpService(): WxMpService {
        val service: WxMpService = WxMpServiceImpl()
        val wxMysqlOps = WxMysqlOps(configService, lockPOService)
        val configStorages = mpProperties.configs.map { mpConfig ->
            val configStorage = WxMpMysqlConfigImpl(wxMysqlOps, mpConfig.appId)
            configStorage.appId = mpConfig.appId.trim()
            configStorage.secret = mpConfig.secret.trim()
            configStorage.token = mpConfig.token.trim()
            configStorage.aesKey = mpConfig.aesKey.trim()
            return@map configStorage
        }

        service.setMultiConfigStorages(configStorages.associateBy { it.appId })
        return service
    }

    @Bean
    fun wxPayService(): WxPayService {
        val payConfig = WxPayConfig()
        payConfig.appId = StringUtils.trimToNull(payProperties.appId)
        payConfig.mchId = StringUtils.trimToNull(payProperties.mchId)
        payConfig.mchKey = StringUtils.trimToNull(payProperties.mchKey)
        payConfig.subAppId = StringUtils.trimToNull(payProperties.subAppId)
        payConfig.subMchId = StringUtils.trimToNull(payProperties.subMchId)
        payConfig.keyPath = StringUtils.trimToNull(payProperties.keyPath)

        // 可以指定是否使用沙箱环境
        payConfig.isUseSandboxEnv = false

        val wxPayService: WxPayService = WxPayServiceImpl()
        wxPayService.config = payConfig
        return wxPayService
    }

}