package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg

@NoArg
@TableName("zy_order")
class ZyOrderPO{
        @TableId
        var id: Long? = null
        var account: String? = null
        var openid: String? = null
        @TableField("actual_order_amount_str")
        var actualOrderAmountStr: String? = null
        @TableField("admin_uid")
        var admin_uid: Int? = null
        @TableField("balance_amount_str")
        var balanceAmountStr: String? = null
        @TableField("card_code")
        var card_code: Any? = null
        @TableField("card_type")
        var card_type: Int? = null
        // 渠道
        var channel: String? = null
        @TableField("created_at")
        var created_at: String? = null
        @TableField("goods_id")
        var goods_id: Int? = null
        // 预估佣金 单位分
        @TableField("income_money")
        var incomeMoney: Int? = null
        // 订单交易额
        var money: Int? = null
        var num: Int? = null
        // 订单号
        @TableField("out_trade_no")
        var outTradeNo: String? = null

        @TableField("pay_at")
        var pay_at: String? = null
        @TableField("pay_platform")
        var pay_platform: String? = null
        @TableField("sku_id")
        var sku_id: Int? = null
        // 2 已支付
        // 3 已完成
        // 5 退款/售后
        var status: Int? = null
        @TableField("three_platform")
        var three_platform: String? = null
        @TableField("three_platform_notify")
        var three_platform_notify: String? = null
        @TableField("three_platform_order_id")
        var three_platform_order_id: String? = null
        @TableField("transaction_id")
        var transaction_id: String? = null
        var uid: Int? = null
        @TableField("updated_at")
        var updated_at: String? = null

        /**
         * goods 中的对象
         */
        var code: String? = null
        @TableField("cost_price")
        var cost_price: Int? = null
        @TableField("is_virtual")
        var is_virtual: Int? = null
        @TableField("logo_url")
        var logo_url: String? = null
        var name: String? = null
        @TableField("origin_price")
        var origin_price: Int? = null
        var price: Int? = null
        @TableField("sale_num")
        var sale_num: Int? = null
        @TableField("stock_price")
        var stock_price: Int? = null
        var type: Int? = null
}