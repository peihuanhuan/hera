package net.peihuan.hera.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "zy")
data class ZyProperties(
        val appid: String
)
