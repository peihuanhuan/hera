package net.peihuan.hera.feign.dto.bilibili

data class Section(
    val episode_id: Int,
    val episodes: List<Episode>,
    val id: Int,
    val title: String,
    val type: Int
)