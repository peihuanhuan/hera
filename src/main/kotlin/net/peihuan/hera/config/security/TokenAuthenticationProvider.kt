package net.peihuan.hera.config.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import mu.KLogging
import net.peihuan.hera.component.JwtTokenComponent
import net.peihuan.hera.constants.JWT_ROLE_FIELD
import net.peihuan.hera.exception.CodeEnum
import net.peihuan.hera.service.UserService
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class TokenAuthenticationProvider(
    private val userService: UserService,
    private val request: HttpServletRequest,
    private val jwtTokenComponent: JwtTokenComponent,
) : AuthenticationProvider {

    companion object : KLogging()

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        if (authentication.isAuthenticated) {
            return authentication
        }
        logger.info { "token: ${authentication.credentials}  req: ${request.servletPath}" }
        // 没有 token ，返回没有权限的空认证，需要登录权限的接口会权限不足
        val credentials = authentication.credentials
            ?: return PreAuthenticatedAuthenticationToken("", "", emptyList())
        val auth: AbstractAuthenticationToken

        val token = credentials as String
        try {
            val claimsFromToken = jwtTokenComponent.getAllClaimsFromToken(token)

            val principal = userPrincipal(claimsFromToken)

            // if (!principal.enable) {
            //     return PreAuthenticatedAuthenticationToken(CodeEnum.DISABLED, token, emptyList())
            // }

            val roles = claimsFromToken[JWT_ROLE_FIELD] as String
            val authorities: Set<SimpleGrantedAuthority> = if (roles.isBlank()) emptySet() else roles.split(",")
                .map { SimpleGrantedAuthority(it) }.toSet()

            auth = PreAuthenticatedAuthenticationToken(principal, token, authorities)
            auth.setAuthenticated(true)
        } catch (e: ExpiredJwtException) {
            return PreAuthenticatedAuthenticationToken(CodeEnum.TOKEN_EXPIRED, token, emptyList())
        } catch (e: Exception) {
            logger.error(e.message, e)
            return PreAuthenticatedAuthenticationToken(CodeEnum.AUTH_INFO_ERROR, token, emptyList())
        }
        return auth
    }

    private fun userPrincipal(claimsFromToken: Claims): String? {
        val userId = claimsFromToken.subject.toLong()
        val simpleUser = userService.getSimpleUser(userId) ?: return null
        return simpleUser.openid
    }

    override fun supports(authentication: Class<*>): Boolean {
        return TokenAuthentication::class.java.isAssignableFrom(authentication)
    }


}