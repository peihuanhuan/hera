package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("bilibili_audio")
data class BilibiliAudioPO (
    @TableId
    var id: Long?=null,
    var taskId: Long,
    var openid: String,
    val bvid: String,
    val aid: String,
    val title: String,
    val mid: String,
    val createTime: Date? = null,
    val updateTime: Date? = null,
)