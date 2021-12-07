package net.peihuan.hera.exception

enum class CodeEnum(val code: Int, val msg: String) {
    SUCCESS(200, "成功"),

    PARAMS_ERROR(400, "参数错误"),
    UNAUTHORIZED(400, "未授权"),
    AUTHORIZED_FAIL(400, "授权失败"),
    HTTP_METHOD_NOT_SUPPORT(400, "方法不支持"),
    SYS_ERROR(500, "系统异常"),

    DEFAULT_BIZ_EXCEPTION(1000, "业务校验异常");

}