package net.peihuan.hera.feign.dto.bilibili

data class View(
    val aid: String,
    val bvid: String,
    val cid: String,
    val copyright: Int,
    val ctime: Int,
    val desc: String,
    // val desc_v2: List<Any>,
    // val dimension: Dimension,
    val duration: Int,
    // val `dynamic`: String,
    // val honor_reply: HonorReply,
    val no_cache: Boolean,
    val owner: Owner,
    // val pages: List<Any>,
    val pic: String,
    val pubdate: Int,
    val rights: Rights,
    // val stat: Stat,
    val state: Int,
    // val subtitle: Subtitle,
    val tid: Int,
    val title: String,
    val tname: String,
    // val user_garb: UserGarb,
    val videos: Int
)