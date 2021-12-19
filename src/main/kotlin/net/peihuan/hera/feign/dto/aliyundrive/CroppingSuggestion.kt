package net.peihuan.hera.feign.dto.aliyundrive

data class CroppingSuggestion(
    val aspect_ratio: String,
    val cropping_boundary: CroppingBoundary,
    val score: Double
)