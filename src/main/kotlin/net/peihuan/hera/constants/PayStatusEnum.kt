package net.peihuan.hera.constants

import com.baomidou.mybatisplus.annotation.EnumValue

enum class PayStatusEnum(@EnumValue val code: Int, val msg: String) {

    DEFAULT(0, "未支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),

}