package net.peihuan.hera.feign.service

import net.peihuan.hera.feign.config.ZhongyongRequestInterceptor
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "fastposter",
    url = "http://81.68.119.197:5000",
    configuration = [ZhongyongRequestInterceptor::class]
)
interface FastposterService {


    data class Request(
        val posterId: String,
        val inviter: String,
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