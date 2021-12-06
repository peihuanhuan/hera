package net.peihuan.hera.util

import org.springframework.beans.BeanUtils

fun <T> T?.tolist(): List<T> {
    return if (this == null) {
        emptyList()
    } else {
        listOf(this)
    }
}

inline fun <reified T> Any.copyPropertiesTo(): T {
    val t = T::class.java.getDeclaredConstructor().newInstance()
    BeanUtils.copyProperties(this, t as Any)
    return t
}