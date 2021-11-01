package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("t_lock")
data class LockPO(
        @TableId
        val lockKey: String
)