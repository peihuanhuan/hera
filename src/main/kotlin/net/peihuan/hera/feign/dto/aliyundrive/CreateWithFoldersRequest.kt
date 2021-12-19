package net.peihuan.hera.feign.dto.aliyundrive

data class CreateWithFoldersRequest(
    val check_name_mode: String,
    val content_hash: String,
    val content_hash_name: String,
    val drive_id: String,
    val name: String,
    val parent_file_id: String,
    val part_info_list: List<PartInfo>,
    val proof_code: String,
    val proof_version: String,
    val size: Int,
    val type: String,
    // 猜测的隐藏
    // val mime_extension: String,
    // val mime_type: String
)