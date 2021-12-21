package net.peihuan.hera.feign.dto.bilibili

data class VideoPage(
    val cid: String,
    val duration: Int,
    val from: String,
    val page: Int,
    val part: String,
    val vid: String,
    val weblink: String
)