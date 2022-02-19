package net.peihuan.hera.constants

import com.baomidou.mybatisplus.annotation.EnumValue

enum class NotifyTypeEnum(@EnumValue val code: Int, val msg: String) {

    MESSAGE_TEMPLATE(1, "消息提醒"),
    MP_REPLY(2, "公众号回复"),

}