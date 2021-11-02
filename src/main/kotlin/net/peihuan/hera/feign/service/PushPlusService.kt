package net.peihuan.hera.feign.service

import net.peihuan.hera.feign.config.ZhongyongRequestInterceptor
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "pushplus", url = "http://www.pushplus.plus", configuration = [ZhongyongRequestInterceptor::class])
interface PushPlusService {

    data class SendRequest(
            val token: String,
            val title: String,
            val channel: String,
            val webhook: String,
            val content: String,
    )

    @PostMapping("send")
    fun send(@RequestBody body: SendRequest): String
}