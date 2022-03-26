package net.peihuan.hera.feign.dto.bilibili

data class Episode(
    val aid: Int?,
    val badge: String?,
    val badge_info: BadgeInfo?,
    val badge_type: Int?,
    val bvid: String?,
    val cid: Int?,
    // val cover: String,
    // val dimension: DimensionX,
    val duration: Int?,
    // val from: String,
    val id: Int?,
    // val is_view_hide: Boolean,
    // val link: String,
    // val long_title: String,
    // val pub_time: Int,
    val pv: Int,
    // val release_date: String,
    // val rights: RightsX,
    val share_copy: String?,
    // val share_url: String,
    // val short_link: String,
    val status: Int?,
    // val subtitle: String,
    val title: String?,
    val vid: String?
)