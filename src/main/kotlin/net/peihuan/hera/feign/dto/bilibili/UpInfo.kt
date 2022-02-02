package net.peihuan.hera.feign.dto.bilibili

data class UpInfo(
    val avatar: String,
    val follower: Int,
    val is_follow: Int,
    val mid: Int,
    val pendant: Pendant,
    val theme_type: Int,
    val uname: String,
    val verify_type: Int,
    val vip_label: VipLabel,
    val vip_status: Int,
    val vip_type: Int
)