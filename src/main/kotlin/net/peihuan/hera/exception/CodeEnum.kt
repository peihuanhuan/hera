package net.peihuan.hera.exception

enum class CodeEnum(val code: Int, val msg: String) {
    SUCCESS(200, "成功"),

    DEFAULT_BIZ_EXCEPTION(1000, "业务校验异常");

}