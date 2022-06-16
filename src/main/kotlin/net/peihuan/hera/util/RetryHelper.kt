package net.peihuan.hera.util

import net.peihuan.hera.exception.BizException


inline fun <reified T> blockWithTry(retryTime: Int = 5, initDelay: Long = 1200, block: () -> T): T {
    var cnt = 0;
    var delay = initDelay
    try {
        while (++cnt < retryTime) {
            return block()
        }
    } catch (e: Exception) {
        Thread.sleep(delay)
        delay += 1000
    }
    throw BizException.buildBizException("方法重试多次失败")


}