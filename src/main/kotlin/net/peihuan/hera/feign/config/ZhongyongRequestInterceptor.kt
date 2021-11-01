package net.peihuan.hera.feign.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value

class ZhongyongRequestInterceptor : RequestInterceptor {

    @Value("\${zy.token}")
    private val token: String? = null
    override fun apply(template: RequestTemplate) {
        template.header("Authorization", token)
    }
}