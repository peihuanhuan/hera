package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("points_record")
data class PointsRecordPO(
        @TableId
        var id: Long? = null,
        val openid: String,
        var points: Int,
        @TableField("`desc`")
        var desc: String? = null,
        val createTime: Date? = null
)