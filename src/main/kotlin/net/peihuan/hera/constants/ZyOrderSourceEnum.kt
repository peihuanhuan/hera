package net.peihuan.hera.constants

import com.baomidou.mybatisplus.annotation.EnumValue

enum class ZyOrderSourceEnum(@EnumValue val code: Int, val msg: String) {

    BUY(1, "购买"),
    EXCHANGE(2, "积分兑换"),
    PRESENT(3, "免费赠送");

    companion object {
        fun getSourceEnum(code: Int): ZyOrderSourceEnum? {
            for (value in values()) {
                if (value.code == code) {
                    return value;
                }
            }
            return null
        }
    }

}