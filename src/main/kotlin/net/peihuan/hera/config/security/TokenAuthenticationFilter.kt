package net.peihuan.hera.config.security

import net.peihuan.hera.constants.HEADER
import net.peihuan.hera.constants.TOKEN_PREFIX
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TokenAuthenticationFilter : OncePerRequestFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, fc: FilterChain) {
        val context = SecurityContextHolder.getContext()
        if (context.authentication == null || !context.authentication.isAuthenticated) {
            val token = req.getHeader(HEADER)
            if (token.isNullOrBlank() || !token.startsWith(TOKEN_PREFIX)) {
                context.authentication = TokenAuthentication(null)
            } else {
                val authenticationToken = token.removePrefix(TOKEN_PREFIX)
                context.authentication = TokenAuthentication(authenticationToken)
            }
        }
        fc.doFilter(req, res)
    }
}