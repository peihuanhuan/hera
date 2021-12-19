package net.peihuan.hera.feign.dto.aliyundrive

data class ImageMediaMetadata(
    val cropping_suggestion: List<CroppingSuggestion>,
    val exif: String,
    val height: Int,
    val image_quality: ImageQuality,
    val image_tags: List<ImageTag>,
    val width: Int
)