package net.peihuan.hera.feign.dto.bilibili

data class Dash(
    val audio: List<Audio>?,
    val duration: Int,
    val minBufferTime: Double,
    val min_buffer_time: Double,
    val video: List<Video>?
)