package net.peihuan.hera.config

import mu.KotlinLogging
import net.peihuan.hera.persistent.service.LockPOService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

class MysqlDistributedLock(val key: String, val lockPOService: LockPOService) : Lock {

    private val log = KotlinLogging.logger {}

    override fun lock() {
        var success = false
        while (!success) {
            success = lockPOService.lock(key)
        }
    }

    override fun lockInterruptibly() {
        lock()
    }

    override fun tryLock(): Boolean {
        try {
            return lockPOService.lock(key)
        } catch (e: Exception) {
            log.warn { e }
        }
        return false
    }

    override fun tryLock(time: Long, unit: TimeUnit): Boolean {
        return tryLock()
    }

    override fun unlock() {
        lockPOService.unlock(key)
    }

    override fun newCondition(): Condition {
        throw UnsupportedOperationException("自定义 Mysql 分布式锁暂不支持此操作")
    }
}