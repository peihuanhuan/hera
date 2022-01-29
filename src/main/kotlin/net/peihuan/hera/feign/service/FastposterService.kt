package net.peihuan.hera.feign.service

import net.peihuan.hera.feign.config.ZhongyongRequestInterceptor
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "fastposter",
    url = "http://101.43.150.221:5000",
    configuration = [ZhongyongRequestInterceptor::class]
)
interface FastposterService {


    data class Request(
        val posterId: String,
        val qrcode: String,
        val head: String,
        val nickname: String
    ) {
        val accessKey = "923db64fc136fef1"
        val secretKey = "c9d838e9b90c4ac7"
    }

    data class Response (
        val code: Int,
        val msg: String,
        val url: String
    )

    @PostMapping("api/link")
    fun generatePoster(@RequestBody request: Request): Response
}