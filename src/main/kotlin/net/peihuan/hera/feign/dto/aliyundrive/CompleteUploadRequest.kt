package net.peihuan.hera.feign.dto.aliyundrive

data class CompleteUploadRequest(
    val drive_id: String,
    val file_id: String,
    val upload_id: String
)