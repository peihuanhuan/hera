package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("red_package")
data class RedPackagePO (
    @TableId
    var id: Long? = null,
    val tradeNo: String,
    val sendName: String,
    val openid: String,
    val totalAmount: Int,
    val wishing: String,
    val actName: String,
    val remark: String,
    val sceneId: String,
    val zyOrderId: Long? = null,
    var payTime: String? = null,
    var paymentNo: String? = null,
    val createTime: Date? = null,
    val updateTime: Date? = null,
)