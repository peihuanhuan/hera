package net.peihuan.hera.feign.dto.bilibili

data class Statistic(
    val collect: Int,
    val comment: Int,
    val play: Int,
    val share: Int,
    val sid: Int
)