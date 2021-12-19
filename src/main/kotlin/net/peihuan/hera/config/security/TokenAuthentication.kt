package net.peihuan.hera.config.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import javax.security.auth.Subject

class TokenAuthentication internal constructor(private val token: String?) : Authentication {
    override fun implies(subject: Subject): Boolean {
        return false
    }

    override fun getName(): String? {
        return null
    }

    override fun getCredentials(): Any? {
        return token
    }

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return null
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return null
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
    }
}