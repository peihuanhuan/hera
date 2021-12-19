package net.peihuan.hera.constants

enum class TaskStatusEnum(val code: Int, val msg: String) {

    DEFAULT(0, "未处理"),
    PROCESS(1, "处理中"),
    SUCCESS(2, "成功"),
    FAIL(3, "失败"),

}