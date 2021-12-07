package net.peihuan.hera.web.filter

import mu.KotlinLogging
import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.exception.CodeEnum
import net.peihuan.hera.util.toJson
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@ServletComponentScan
@WebFilter(urlPatterns = ["/admin/*"], filterName = "authorizationTokenFilter")
class AuthorizationTokenFilter : Filter {
    private val log = KotlinLogging.logger {}

    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val request: HttpServletRequest = req as HttpServletRequest
        val response: HttpServletResponse = res as HttpServletResponse
        val url: String = request.getRequestURI()
        if (url == "/admin/login") {
            chain.doFilter(request, response)
            return
        }
        val authToken: String? = request.getHeader("X-Token")
        if (authToken == null) {
            val resultVO = JsonResult.error(CodeEnum.UNAUTHORIZED)
            responseWriteJson(resultVO, response)
            return
        }
        if (authToken != "68CEA7E4-2D70-4C65-9D35-F3C54EBF9D0C") {
            val resultVO = JsonResult.error(CodeEnum.AUTHORIZED_FAIL)
            responseWriteJson(resultVO, response)
            return
        }
        chain.doFilter(request, response)
    }

    fun responseWriteJson(obj: Any, response: HttpServletResponse) {
        printOut(obj, response, HttpStatus.OK)
    }

    private fun printOut(obj: Any, response: HttpServletResponse, httpStatus: HttpStatus) {
        response.characterEncoding = "UTF-8"
        response.status = httpStatus.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        try {
            response.writer.use { printWriter -> printWriter.write(obj.toJson()) }
        } catch (e: Exception) {
            log.error("写出响应发生异常", e)
        }
    }
}