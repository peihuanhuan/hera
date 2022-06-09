package net.peihuan.hera.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "hera")
data class HeraProperties(
        val cacheExpireSeconds: Long,
        // val wechatMediaid: String,
        val adminOpenid: String
)
