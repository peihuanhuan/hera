package net.peihuan.hera.constants

import com.baomidou.mybatisplus.annotation.EnumValue

enum class BilibiliTaskOutputTypeEnum(@EnumValue val code: Int, val msg: String) {

    AUDIO(0, "仅音频"),
    VIDEO(1, "音频+视频"),
    ;

    companion object {
        fun getTypeEnum(code: Int): BilibiliTaskOutputTypeEnum? {
            for (value in values()) {
                if (value.code == code) {
                    return value;
                }
            }
            return null
        }
    }
}