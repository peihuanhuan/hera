package net.peihuan.hera.service.remote

import mu.KotlinLogging
import net.peihuan.hera.feign.dto.shortUrl.GenerateShortUrlReq
import net.peihuan.hera.feign.dto.shortUrl.ShortUrlResp
import net.peihuan.hera.feign.service.ShortUrlFeignService
import net.peihuan.hera.util.toBean
import org.springframework.stereotype.Service


@Service
class ShortUrlRemoteServiceWrapper(private val shortUrlFeignService: ShortUrlFeignService) {

    private val log = KotlinLogging.logger {}

    fun getShortUrl(url: String): String? {
        try {
            // 接口返回的是 text/plain; charset=utf-8  必须手动序列化
            val generateShortUrl = shortUrlFeignService.generateShortUrl(GenerateShortUrlReq(url))
            val resp: ShortUrlResp = generateShortUrl.toBean()
            return resp.data?.short_uri
        } catch (e: Exception) {
            log.error(e.message, e)
            return null
        }
    }


}