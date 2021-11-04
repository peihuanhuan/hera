package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

@TableName("subscribe")
data class SubscribePO(
        @TableId
        var id: Long? = null,
        val appid: String? = null,
        val openid: String? = null,
        val subscribeScene: Int? = null,
        var qrScene: String? = null,
        var qrSceneStr: String? = null,
        var status: Int? = null,
        val createTime: Date ? = null,
        var updateTime: Date ? = null,
)