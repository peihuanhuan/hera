package net.peihuan.hera.feign.dto.aliyundrive

data class ListFileRequest(
    val all: Boolean = false,
    val drive_id: String,
    val fields: String = "*",
    val image_thumbnail_process: String ="image/resize,w_400/format,jpeg",
    val image_url_process: String = "image/resize,w_1920/format,jpeg",
    val limit: Int,
    val order_by: String = "updated_at",
    val order_direction: String = "DESC",
    val parent_file_id: String,
    val url_expire_sec: Int = 1600,
    val video_thumbnail_process: String = "video/snapshot,t_1000,f_jpg,ar_auto,w_300"
)