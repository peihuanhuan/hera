package net.peihuan.hera.constants

import com.baomidou.mybatisplus.annotation.EnumValue

enum class TaskStatusEnum(@EnumValue val code: Int, val msg: String) {

    DEFAULT(0, "未处理"),
    PROCESS(1, "处理中"),
    SUCCESS(2, "成功"),
    FAIL(3, "失败")

    ;

    companion object {
        fun getTypeEnum(code: Int): TaskStatusEnum? {
            for (value in values()) {
                if (value.code == code) {
                    return value;
                }
            }
            return null
        }
    }
}