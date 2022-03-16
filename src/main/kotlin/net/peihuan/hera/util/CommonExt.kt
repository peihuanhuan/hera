package net.peihuan.hera.util

import mu.KotlinLogging
import net.peihuan.hera.constants.YYYYMMDDHHMMSS
import org.joda.time.DateTime
import org.springframework.beans.BeanUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.net.URL
import java.util.*
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

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


fun printOut(obj: Any, response: HttpServletResponse, httpStatus: HttpStatus) {
    response.characterEncoding = "UTF-8"
    response.status = httpStatus.value()
    response.contentType = MediaType.APPLICATION_JSON_VALUE
    try {
        response.writer.use { printWriter -> printWriter.write(obj.toJson()) }
    } catch (e: Exception) {
        log.error("写出响应发生异常", e)
    }
}

fun randomOutTradeNo(): String {
    return DateTime.now().toString(YYYYMMDDHHMMSS) + UUID.randomUUID().toString().replace("-", "").substring(0, 10)
}

fun getUrlParams(url: String) :Map<String, String> {
    var u = url
    if (!url.startsWith("https://") && !url.startsWith("http://")) {
        u = "http://" + url
    }

    val query = URL(u).query
    val keyValues = query?.split("&")
    val map = mutableMapOf<String, String>()
    keyValues?.forEach {
        val split = it.split("=")
        map[split[0]] = split[1]
    }
    return map
}