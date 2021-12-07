package net.peihuan.hera.web.filter

import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@ServletComponentScan
@WebFilter(urlPatterns = ["/*"], filterName = "aaaCorsFilter")
class AaaCorsFilter : Filter {

    override fun doFilter(req: ServletRequest, resp: ServletResponse, chain: FilterChain) {
        val httpResponse = resp as HttpServletResponse
        val reqs = req as HttpServletRequest
        //解决跨域问题
        httpResponse.setHeader(
            "Access-Control-Allow-Headers",
            "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-TOKEN"
        )
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true")
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE")
        httpResponse.addHeader("Access-Control-Allow-Origin", reqs.getHeader("Origin"))
        //httpResponse.addHeader("Access-Control-Expose-Headers","Msg,Code");
        chain.doFilter(req, httpResponse)
    }

}