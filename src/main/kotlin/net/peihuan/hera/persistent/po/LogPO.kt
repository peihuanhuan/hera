package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

@TableName("log")
class LogPO {
    @TableId
    val id: Long? = null
    var log: String = ""

    val createTime: Date? = null
    var updateTime: Date? = null

}