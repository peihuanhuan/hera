package net.peihuan.hera.feign.dto.bilibili

enum class Quality(val code: Int, val internalName: String, val displayName: String) {
    HDR(125, "HDR", "真彩 HDR"),
    FOUR_K(120, "4K", "超清 4K"),
    P60_1080(116, "1080P60", "高清 1080P60"),
    P_PLUS_1080(112, "1080P+", "高清 1080P"),
    P_1080(80, "1080P", "高清 1080P"),
    P60_720(74, "720P60", "高清 720P60"),
    P_720(64, "720P", "高清 720P"),
    P_480(32, "480P", "清晰 480P"),
    P_360(16, "360P", "流畅 360P"),
}