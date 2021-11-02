package net.peihuan.hera.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "wx.mp")
data class WxMpProperties(
        val configs: List<MpConfig>,
        // val orderStatusTemplateid: String,
        // val leaveMessageTemplateid: String
)


data class MpConfig(
        /**
         * 设置微信公众号的appid
         */
        val appId: String,

        /**
         * 设置微信公众号的app secret
         */
        val secret: String,

        /**
         * 设置微信公众号的token
         */
        val token: String,

        /**
         * 设置微信公众号的EncodingAESKey
         */
        val aesKey: String,
)