package net.peihuan.hera.feign.dto.bilibili

data class Durl(
    val ahead: String,
    val backup_url: List<String>,
    val length: Int,
    val order: Int,
    val size: Int,
    val url: String,
    val vhead: String
)