package net.peihuan.hera.util

import org.springframework.security.core.context.SecurityContextHolder


/**
 * 获取当前用户登录信息
 */

val currentUserThreadLocal = ThreadLocal<String>()

fun setCurrentUser(openid: String) {
    currentUserThreadLocal.set(openid)
}

fun removeCurrentUser() {
    currentUserThreadLocal.remove()
}

val currentUserOpenid: String
    get() {
        if (currentUserThreadLocal.get() != null) {
            return currentUserThreadLocal.get()
        }
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication.principal is String) {
            authentication.principal as String
        } else {
            throw UnsupportedOperationException("没有登录信息")
        }
    }
