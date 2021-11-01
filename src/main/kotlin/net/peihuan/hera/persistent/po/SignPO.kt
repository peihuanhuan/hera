package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

@TableName("sign")
data class SignPO(
        @TableId
        val id: Long? = null,
        val openid: String,
        val points: Int,
        val createTime: Date? = null,
        val updateTime: Date? = null
)