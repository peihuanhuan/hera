package net.peihuan.hera.feign.dto.bilibili

data class NewEp(
    val desc: String,
    val id: Int,
    val is_new: Int,
    val title: String
)