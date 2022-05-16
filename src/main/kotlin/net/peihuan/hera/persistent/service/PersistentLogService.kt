package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.LogMapper
import net.peihuan.hera.persistent.po.LogPO
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class PersistentLogService : ServiceImpl<LogMapper, LogPO>() {

    @Async
    fun saveLog(log: String) {
        val po = LogPO()
        po.log = log
        save(po)
    }

}