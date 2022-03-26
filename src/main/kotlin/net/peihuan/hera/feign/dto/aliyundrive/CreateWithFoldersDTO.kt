package net.peihuan.hera.feign.dto.aliyundrive

data class CreateWithFoldersDTO(
    val domain_id: String,
    val drive_id: String,
    val encrypt_mode: String,
    val file_id: String,
    val file_name: String,
    val location: String?,
    val parent_file_id: String,
    val part_info_list: List<PartInfoX>?,
    val rapid_upload: Boolean?,
    val type: String,
    val upload_id: String?
)