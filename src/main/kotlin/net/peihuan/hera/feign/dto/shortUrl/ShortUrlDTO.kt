package net.peihuan.hera.feign.dto.shortUrl

data class ShortUrlDTO(
    val code: String,
    val created_at: String,
    val short_uri: String,
    val url: String
)