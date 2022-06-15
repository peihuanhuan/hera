package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("wx_ent_pay_order")
data class EntPayOrderPO(
    @TableId
    var id: Long? = null,
    val partnerTradeNo: String,
    val openid: String,
    val amount: Int,
    val description: String,
    val checkName: String,
    val zyOrderId: Long,
    var payTime: String? = null,
    var paymentNo: String? = null,
    val createTime: Date? = null,
    var updateTime: Date? = null,
)