package net.peihuan.hera.feign.dto.bilibili

data class Honor(
    val aid: Int,
    val desc: String,
    val type: Int,
    val weekly_recommend_num: Int
)