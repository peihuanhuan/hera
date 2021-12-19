package net.peihuan.hera.feign.dto.aliyundrive

data class ShareDTO(
    val created_at: String,
    val creator: String,
    val description: String,
    val download_count: Int,
    val drive_id: String,
    val expiration: String,
    val expired: Boolean,
    val file_id: String,
    val file_id_list: List<String>,
    val preview_count: Int,
    val save_count: Int,
    val share_id: String,
    val share_msg: String,
    val share_name: String,
    val share_policy: String,
    val share_pwd: String,
    val share_url: String,
    val status: String,
    val updated_at: String
)