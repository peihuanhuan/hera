package net.peihuan.hera.config.security.config

import net.peihuan.hera.config.security.TokenAuthenticationFilter
import net.peihuan.hera.config.security.TokenAuthenticationProvider
import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.exception.CodeEnum
import net.peihuan.hera.util.printOut
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    val tokenAuthenticationFilter: TokenAuthenticationFilter,
    val tokenAuthenticationProvider: TokenAuthenticationProvider
) : WebSecurityConfigurerAdapter() {


    @Throws(Exception::class)
    public override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(tokenAuthenticationProvider)
    }

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity // 禁用 CSRF
            .csrf().disable() // 不创建会话
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // 授权异常
            .exceptionHandling()
            .authenticationEntryPoint { _: HttpServletRequest?, response: HttpServletResponse, e: AuthenticationException ->
                printOut(
                    JsonResult.error(CodeEnum.AUTH_INFO_ERROR,), response, HttpStatus.OK
                )
            } // 过滤请求
            .and().authorizeRequests() // 所有请求都需要认证
            .anyRequest().authenticated()
    }
}