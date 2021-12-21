package net.peihuan.hera.feign.dto.bilibili

data class DashPlayUrl(
    val accept_description: List<String>,
    val accept_format: String,
    val accept_quality: List<Int>,
    val dash: Dash?,
    val durl: List<Durl>?,
    val format: String,
    val from: String,
    val message: String,
    val quality: Int,
    val result: String,
    val seek_param: String,
    val seek_type: String,
    val support_formats: List<SupportFormat>,
    val timelength: Int,
    val video_codecid: Int
)