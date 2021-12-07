package net.peihuan.hera.domain

import net.peihuan.hera.exception.BizException
import net.peihuan.hera.exception.CodeEnum

data class JsonResult(
        val code: Int,
        var data: Any?,
        val msg: String = ""
) {
    companion object {
        fun success(data: Any? = null): JsonResult {
            return JsonResult(code = CodeEnum.SUCCESS.code, data = data, msg = "success")
        }

        fun error(e: BizException): JsonResult {
            return JsonResult(code = e.code, data = null, msg = e.msg?:"空msg")
        }

        fun error(e: CodeEnum): JsonResult {
            return JsonResult(code = e.code, data = null, msg = e.msg?:"空msg")
        }

        fun error(code: Int, msg: String): JsonResult {
            return JsonResult(code = code, data = null, msg = msg)
        }
    }

    fun add(key: String ,value: Any): JsonResult {
        if (data == null) {
            data = mutableMapOf<String, Any>()
        }
        if (data !is MutableMap<*, *>) {
            throw RuntimeException("JsonResult data 类型不匹配")
        }
        (data as MutableMap<String, Any>)[key] = value
        return this
    }
}