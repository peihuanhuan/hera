package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("bilibili_audio")
data class BilibiliSubTaskPO (
    @TableId
    var id: Long?=null,
    var taskId: Long,
    var openid: String,
    val bvid: String,
    val duration: Int,
    val aid: String,
    val cid: String,
    val sid: String?=null,
    val title: String,
    val mid: String,
    var fileId: String? = null,   // 阿里云盘fileId
    var baiduFileId: String? = null,
    val createTime: Date? = null,
    var updateTime: Date? = null,
)