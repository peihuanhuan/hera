package net.peihuan.hera.feign.dto

data class ZyResponse<T>(
        val code: Int?,
        val msg: String?,
        val data: T?
)