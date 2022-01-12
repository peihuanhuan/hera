package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

@TableName("config")
class ConfigPO {
    @TableId
    val id: Long? = null
    val appid: String? = null

    @TableField("`key`")
    lateinit var key: String

    @TableField("`value`")
    var value: String? = null
    var expireAt: Date? = null

    @TableLogic
    val deleted: Boolean? = null
    val createTime: Date? = null
    var updateTime: Date? = null


    fun isExpired(): Boolean {
        return if (expireAt == null) {
            false
        } else {
            expireAt!!.time < System.currentTimeMillis()
        }
    }
}