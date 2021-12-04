package net.peihuan.hera.feign.dto

import net.peihuan.hera.domain.annotation.NoArg

@NoArg
data class ZyOrder(
    val account: String? = null,
    val actualOrderAmountStr: String? = null,
    val admin_uid: Int? = null,
    val balanceAmountStr: String? = null,
    val card_code: Any? = null,
    val card_type: Int? = null,
    val channel: String? = null,
    val created_at: String? = null,
    val err_code_des: Any? = null,
    val goods: Goods? = null,
    val goods_id: Int? = null,
    val income_money: Int? = null,
    val money: Int? = null,
    val num: Int? = null,
    val out_trade_no: String? = null,
    val pay_at: String? = null,
    val pay_platform: String? = null,
    val sku_id: Int? = null,
    // 2 已支付
    // 3 已完成
    // 5 退款/售后
    // 6 已退款
    val status: Int? = null,
    val statusStr: String? = null,
    val three_platform: String? = null,
    val three_platform_notify: String? = null,
    val three_platform_order_id: String? = null,
    val transaction_id: String? = null,
    val uid: Int? = null,
    val updated_at: String? = null,
    val user: User? = null
)

data class Goods(
    val code: String? = null,
    val cost_price: Int? = null,
    val created_at: String? = null,
    val id: Int? = null,
    val is_virtual: Int? = null,
    val logo_url: String? = null,
    val name: String? = null,
    val origin_price: Int? = null,
    val placeholder: String? = null,
    val price: Int? = null,
    val sale_num: Int? = null,
    val status: Int? = null,
    val stock_price: Int? = null,
    val thumb: String? = null,
    val type: Int? = null,
    val updated_at: String? = null,
    val use_rules: Any? = null
)

data class User(
    val admin_uid: Int? = null,
    val avatarUrl: String? = null,
    val channel: String? = null,
    val cpc: String? = null,
    val created_at: String? = null,
    val gender: Int? = null,
    val id: Int? = null,
    val nickName: String? = null,
    val phone: Any? = null,
    val save_money: Int? = null,
    val updated_at: String? = null
)