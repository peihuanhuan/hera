package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("activity")
data class ActivityPO (
    @TableId
    var id: Long,
    var name: String? = null,
    @TableField("`desc`")
    val desc: String? = null,
    val keyword: String,
    val disable: Boolean,
    val createTime: Date? = null,
    val updateTime: Date? = null,
    val startTime: Date,
    val endTime: Date,
)