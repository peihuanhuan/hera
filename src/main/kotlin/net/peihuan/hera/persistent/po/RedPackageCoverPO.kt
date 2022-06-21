package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("red_package_cover")
data class RedPackageCoverPO (
    @TableId
    var id: Long? = null,
    val url: String,
    val style: Int,
    val openid: String? = null,
    var giveUp: Boolean,
    val createTime: Date? = null,
    val updateTime: Date? = null,
)