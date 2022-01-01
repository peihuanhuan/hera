package net.peihuan.hera.web.controller

import mu.KotlinLogging
import net.peihuan.hera.constants.HEADER
import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.exception.CodeEnum
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class CommonExceptionHandler(val req: HttpServletRequest) {


    private val log = KotlinLogging.logger {}

    @ExceptionHandler(value = [BizException::class])
    fun badRequestException(e: BizException): JsonResult {
        return JsonResult.error(e)
    }

    @ExceptionHandler(Exception::class, Throwable::class)
    fun handlerException(e: Exception?): JsonResult {
        log.error("服务器未知异常", e)
        return JsonResult.error(CodeEnum.SYS_ERROR)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handlerAccessDeniedException(e: AccessDeniedException): Any {
        return if (req.getHeader(HEADER).isNullOrBlank()) {
            JsonResult.error(CodeEnum.TOKEN_NOT_EXISTS)
        } else {
            val principal = SecurityContextHolder.getContext().authentication.principal
            if (principal is CodeEnum) {
                JsonResult.error(principal)
            } else {
                JsonResult.error(CodeEnum.FORBIDDEN)
            }
        }
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handlerException(e: MethodArgumentTypeMismatchException): JsonResult {
        return JsonResult.error(CodeEnum.PARAMS_ERROR.code, e.message?:"")
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun messageNotReadableException(e: HttpMessageNotReadableException): JsonResult {
        log.error(e.message, e)
        return JsonResult.error(CodeEnum.PARAMS_ERROR)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): JsonResult {
        log.error(e.message, e)
        val str =
            Objects.requireNonNull<Array<String>>(e.getBindingResult().getAllErrors().get(0).getCodes())[1].split("\\.")
                .toTypedArray()
        return JsonResult.error(
            CodeEnum.PARAMS_ERROR.code, str[1] + ":" + e.getBindingResult()
                .getAllErrors().get(0).getDefaultMessage()
        )
    }

    @ExceptionHandler(BindException::class)
    fun handleMethodArgumentNotValidException(e: BindException): JsonResult {
        log.error(e.message, e)
        val str = Objects.requireNonNull(e.bindingResult.allErrors[0].codes)[1].split("\\.").toTypedArray()
        return JsonResult.error(
            CodeEnum.PARAMS_ERROR.code, str[1] + ":" + e.bindingResult
                .allErrors[0].defaultMessage
        )
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodArgumentNotValidException(e: HttpRequestMethodNotSupportedException): JsonResult {
        log.info("http方法不支持 e={}", e.message)
        return JsonResult.error(CodeEnum.HTTP_METHOD_NOT_SUPPORT)
    }
}