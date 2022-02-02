package net.peihuan.hera.feign.dto.bilibili

data class Season(
    val badge: String,
    val badge_info: BadgeInfoX,
    val badge_type: Int,
    val cover: String,
    val horizontal_cover_1610: String,
    val horizontal_cover_169: String,
    val media_id: Int,
    val new_ep: NewEpX,
    val season_id: Int,
    val season_title: String,
    val season_type: Int,
    val stat: StatX
)