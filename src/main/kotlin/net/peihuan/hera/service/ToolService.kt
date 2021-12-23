package net.peihuan.hera.service

import mu.KotlinLogging
import net.peihuan.hera.constants.YYYY_MM_DD
import net.peihuan.hera.feign.service.ShortUrlFeignService
import org.joda.time.DateTime
import org.springframework.stereotype.Service

@Service
class ToolService(
    private val shortUrlFeignService: ShortUrlFeignService
) {

    private val logger = KotlinLogging.logger {}

    fun tryShortUrl(longUrl: String, expireMonth: Int = 3): String {
        return try {
            val shortUrl = shortUrlFeignService.shortUrl(
                url = longUrl,
                apikey = "61c4157a2c9e6f690fc2fe738c@b10c8a92a58f40dca59012a6a8cb0599",
                domain = "u.fsjobhr.cn",
                expireDate = DateTime.now().plusMonths(expireMonth).toString(YYYY_MM_DD)
            )
            if (shortUrl.contains("http")) {
                shortUrl
            } else {
                longUrl
            }
        } catch (e: Exception) {
            logger.error(e.message, e)
            longUrl
        }
    }

}