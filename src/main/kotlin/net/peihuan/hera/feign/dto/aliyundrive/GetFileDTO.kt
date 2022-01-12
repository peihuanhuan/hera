package net.peihuan.hera.feign.dto.aliyundrive

data class GetFileDTO(
    val file_id: String,
    val name: String,
    val trashed: Boolean
)