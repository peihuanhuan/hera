package net.peihuan.hera.util

import org.springframework.security.core.context.SecurityContextHolder


/**
 * 获取当前用户登录信息
 */
val currentUserOpenid: String
    get() {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication.principal is String) {
            authentication.principal as String
        } else {
            throw UnsupportedOperationException("没有登录信息")
        }
    }
