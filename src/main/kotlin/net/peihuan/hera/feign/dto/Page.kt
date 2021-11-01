package net.peihuan.hera.feign.dto

data class Page<T>(
        val list: List<T>,
        val total: Int,
        val pageNum: Int
)