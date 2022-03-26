package net.peihuan.hera.feign.dto.aliyundrive

data class CreateWithFoldersRequest(
    val check_name_mode: String? = null,
    val content_hash: String? = null,
    val content_hash_name: String? = null,
    val drive_id: String,
    val name: String,
    val parent_file_id: String,
    val part_info_list: List<PartInfo>? = null,
    val proof_code: String? = null,
    val proof_version: String? = null,
    val size: Int? = null,
    val type: String,
    // 猜测的隐藏
    // val mime_extension: String,
    // val mime_type: String
)