package net.peihuan.hera.feign.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "shortKey", url = "http://api.suolink.cn/api.htm")
interface ShortUrlFeignService {

    @GetMapping("")
    fun shortUrl(@RequestParam("url") url: String,
                 @RequestParam("key") apikey: String,
                 @RequestParam("domain") domain: String,
                 @RequestParam("expireDate") expireDate: String): String
}