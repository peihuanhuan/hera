package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("user_tag")
data class UserTagPO (
    @TableId
    var id: Long? = null,
    val openid: String? = null,
    var tagid: Long? = null,
    val createTime: Date? = null,
    val updateTime: Date? = null,

    @TableLogic
    val deleted: Boolean? = null
)