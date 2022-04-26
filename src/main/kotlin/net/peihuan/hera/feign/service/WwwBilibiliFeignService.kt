package net.peihuan.hera.feign.service

import net.peihuan.hera.feign.dto.bilibili.MusicInfo
import net.peihuan.hera.feign.dto.bilibili.MusicUrl
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "wwwbilibili",
    url = "https://www.bilibili.com",
)
interface WwwBilibiliFeignService {


    data class Response<T>(
        val code: Int,
        val msg: String,
        val data: T
    )

    @GetMapping("audio/music-service-c/web/url")
    fun getMusicUrl(
        @RequestParam("sid") sid: String,
        @RequestParam("privilege") privilege: String,
        @RequestParam("quality") quality: String
    ): Response<MusicUrl>

    @GetMapping("audio/music-service-c/web/song/info")
    fun getMusicInfo(@RequestParam("sid") sid: String): Response<MusicInfo>

}