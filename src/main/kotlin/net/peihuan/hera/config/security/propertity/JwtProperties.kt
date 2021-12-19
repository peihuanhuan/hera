package net.peihuan.hera.config.security.propertity

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("security.jwt")
data class JwtProperties(
    val secret: String,
    val expireMinute: Int = 7 * 24 * 60,
    val refreshExpireMinute: Int = 60 * 24 * 15,
)