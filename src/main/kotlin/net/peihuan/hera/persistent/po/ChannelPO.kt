package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.constants.ZyOrderSourceEnum

@TableName("channel")
data class ChannelPO(
    @TableId
    val id: Long,
    val source: ZyOrderSourceEnum,
    val openid: String
)