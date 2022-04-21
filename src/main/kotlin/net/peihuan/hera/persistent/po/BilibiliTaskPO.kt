package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.constants.BilibiliTaskOutputTypeEnum
import net.peihuan.hera.constants.NotifyTypeEnum
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("bilibili_audio_task")
data class BilibiliTaskPO (
    @TableId
    var id: Long?=null,
    var openid: String,
    var request: String,
    var name: String,
    val type: Int,
    val outputType: BilibiliTaskOutputTypeEnum,
    val notifyType: NotifyTypeEnum,
    val size: Int,
    var status: Int,
    var url: String,
    val createTime: Date? = null,
    var updateTime: Date? = null,
)