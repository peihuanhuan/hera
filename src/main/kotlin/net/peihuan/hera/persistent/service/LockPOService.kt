package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import mu.KotlinLogging
import net.peihuan.hera.persistent.mapper.LockMapper
import net.peihuan.hera.persistent.po.LockPO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class LockPOService : ServiceImpl<LockMapper, LockPO>() {

    private val logger = KotlinLogging.logger {}

    fun lock(lock: String): Boolean {
        val po = LockPO(lock)
        try {
            return save(po)
        } catch (e: Exception) {
            logger.warn { e }
        }
        return false
    }

    fun unlock(lock: String): Boolean {
        try {
            return remove(KtQueryWrapper(LockPO::class.java).eq(LockPO::lockKey, lock))
        } catch (e: Exception) {
            logger.warn { e }
        }
        return false
    }


}