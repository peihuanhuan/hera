package net.peihuan.hera.config

import me.chanjar.weixin.common.redis.WxRedisOps
import net.peihuan.baiduPanSDK.service.BaiduOps
import net.peihuan.hera.persistent.service.LockPOService
import net.peihuan.hera.service.ConfigService
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock

@Service
class WxMysqlOps(val configService: ConfigService,
                 val lockPOService: LockPOService) : WxRedisOps, BaiduOps {

    override fun getValue(key: String): String? {
        val configPO = configService.getByKey(key) ?: return null
        return configPO.value
    }

    override fun setValue(key: String, value: String, expire: Int, timeUnit: TimeUnit) {
        configService.updateConfig(key, value, expire.toLong(), timeUnit)
    }

    override fun getExpire(key: String): Long {
        val configPO = configService.getByKey(key) ?: return 0
        if (configPO.isExpired()) {
            return configPO.expireAt!!.time
        } else {
            return System.currentTimeMillis() + 100000 //给个不过期的时间
        }

    }

    override fun expire(key: String, expire: Int, timeUnit: TimeUnit) {
        configService.updateConfig(key, null, expire.toLong(), timeUnit)
    }

    override fun getLock(key: String): Lock {
        return MysqlDistributedLock(key, lockPOService)
    }
}