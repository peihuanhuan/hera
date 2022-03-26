package net.peihuan.hera.domain

class BilibiliSubTask(
    val bilibiliVideo: BilibiliVideo,
    var taskId: Long? = null,
    var openid: String,

    var aliyundriverFileId: String ?= null
) {
    val duration: Int
        get() = bilibiliVideo.duration!!
}