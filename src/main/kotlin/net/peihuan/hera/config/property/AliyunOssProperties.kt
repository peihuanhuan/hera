package net.peihuan.hera.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "aliyunoss")
data class AliyunOssProperties(
        val endpoint: String,
        val accessKeyId: String,
        val accessKeySecret: String,
        val bucketName: String,
        val cdnHost: String
)
