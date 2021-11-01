package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("user_points")
data class UserPointsPO (
    @TableId
    var id: Long? = null,
    val openid: String? = null,
    var points: Int? = null,
    val createTime: Date? = null,
    val updateTime: Date? = null,
)