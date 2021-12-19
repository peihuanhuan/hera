package net.peihuan.hera.feign.dto.aliyundrive

data class ImageTag(
    val centric_score: Double,
    val confidence: Double,
    val name: String,
    val parent_name: String,
    val tag_level: Int
)