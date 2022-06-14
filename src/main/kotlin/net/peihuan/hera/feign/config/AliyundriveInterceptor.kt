package net.peihuan.hera.feign.config

import feign.RequestInterceptor
import feign.RequestTemplate
import net.peihuan.hera.service.share.AliyundriveService

class AliyundriveInterceptor() : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        template.header("authorization", AliyundriveService.ALIYUN_DRIVER_TOKEN)
        template.header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
    }
}