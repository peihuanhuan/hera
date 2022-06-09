package net.peihuan.hera.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "wx.pay")
class WxPayProperties(
    /**
     * 设置微信公众号或者小程序等的appid
     */
    val appId: String,

    /**
     * 微信支付商户号
     */
    val mchId: String?,

    /**
     * 微信支付商户密钥
     */
    val mchKey: String?,

    /**
     * 服务商模式下的子商户公众账号ID，普通模式请不要配置，请在配置文件中将对应项删除
     */
    val subAppId: String? = null,

    /**
     * 服务商模式下的子商户号，普通模式请不要配置，最好是请在配置文件中将对应项删除
     */
    val subMchId: String? = null,

    /**
     * apiclient_cert.p12文件的绝对路径，或者如果放在项目中，请以classpath:开头指定
     */
    val keyPath: String? = null
)