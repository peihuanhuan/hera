package net.peihuan.hera.exception

import java.lang.RuntimeException
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.exception.CodeEnum

enum class CodeEnum(val code: String, val msg: String) {
    DEFAULT_BIZ_EXCEPTION("1000", "业务校验异常");

}