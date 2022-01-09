package net.peihuan.hera.constants

import com.baomidou.mybatisplus.annotation.EnumValue

enum class OrderTypeEnum(@EnumValue val code: Int, val msg: String) {

    RED_PACKAGE(1, "红包封面")

    ;

    companion object {
        fun getTypeEnum(code: Int): OrderTypeEnum? {
            for (value in values()) {
                if (value.code == code) {
                    return value;
                }
            }
            return null
        }
    }

}