package net.peihuan.hera.feign.dto.aliyundrive

data class GetFileRequest(
    val drive_id: String,
    val file_id: String,
)