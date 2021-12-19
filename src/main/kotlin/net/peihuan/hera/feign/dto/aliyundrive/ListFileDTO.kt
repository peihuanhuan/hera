package net.peihuan.hera.feign.dto.aliyundrive

data class ListFileDTO(
    val items: List<Item>,
    val next_marker: String,
    val punished_file_count: Int
)