package net.peihuan.hera.feign.service

import net.peihuan.hera.feign.dto.shortUrl.GenerateShortUrlReq
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "shorturl",
    // url = "https://r.nsini.com/",
    url = "http://u.dejavuu.cn/",
)
interface ShortUrlFeignService {


    @PostMapping("")
    fun generateShortUrl(@RequestBody req: GenerateShortUrlReq): String


}