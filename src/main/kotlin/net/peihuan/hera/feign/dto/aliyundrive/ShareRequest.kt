package net.peihuan.hera.feign.dto.aliyundrive

data class ShareRequest(
    val drive_id: String,
    val expiration: String,
    val file_id_list: List<String>,
    val sync_to_homepage: Boolean = false
)