package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.constants.OrderTypeEnum
import net.peihuan.hera.constants.PayStatusEnum
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("wx_order")
data class WxOrderPO(
    @TableId
    var id: Long? = null,
    val openid: String,
    val outTradeNo: String,
    val transactionId: String? = null,
    var type: OrderTypeEnum,
    var outId: Long? = null,
    var totalFee: Int,
    var payFee: Int,
    var status: PayStatusEnum,
    var payTime: Date? = null,
    val createTime: Date? = null,
    var updateTime: Date? = null,
)