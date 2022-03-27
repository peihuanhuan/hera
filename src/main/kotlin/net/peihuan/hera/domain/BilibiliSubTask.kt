package net.peihuan.hera.domain

import java.util.concurrent.TimeUnit

class BilibiliSubTask(
    private val bilibiliVideo: BilibiliVideo,
    var id: Long? = null,
    var taskId: Long? = null,
    var openid: String,
    var aliyundriverFileId: String? = null
) {
    val aid: String
        get() = bilibiliVideo.aid!!

    val bvid: String
        get() = bilibiliVideo.bvid

    val cid: String
        get() = bilibiliVideo.cid!!

    val duration: Int
        get() = bilibiliVideo.duration!!

    val mid: String
        get() = bilibiliVideo.mid!!

    val originalTitle: String
        get() = bilibiliVideo.title ?: "无标题"

    val trimTitle: String
        get() {
            var title = originalTitle.replace("/", "")
            if (title.length > 40) {
                // linux 文件名最大 255 个字符，截取一部分
                title = title.substring(0, 20) + "..." + title.substring(title.length - 20)
            }
            return title
        }

    // 根据时长选择特定的比特率
    val byteRate: Long
        get() {
            return if (duration < TimeUnit.MINUTES.toSeconds(10)) {
                192
            } else if (duration < TimeUnit.MINUTES.toSeconds(100)) {
                128
            } else if (duration < TimeUnit.MINUTES.toSeconds(200)) {
                64
            } else if (duration < TimeUnit.MINUTES.toSeconds(400)) {
                32
            } else if (duration < TimeUnit.MINUTES.toSeconds(800)) {
                16
            } else {
                16
            }
        }
}