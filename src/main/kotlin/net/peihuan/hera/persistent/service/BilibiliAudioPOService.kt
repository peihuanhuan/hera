package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.BilibiliAudioMapper
import net.peihuan.hera.persistent.po.BilibiliSubTaskPO
import org.springframework.stereotype.Service

@Service
class BilibiliAudioPOService : ServiceImpl<BilibiliAudioMapper, BilibiliSubTaskPO>() {

    fun findByTaskId(taskId: Long): List<BilibiliSubTaskPO> {
        return list(KtQueryWrapper(BilibiliSubTaskPO::class.java).eq(BilibiliSubTaskPO::taskId, taskId))
    }


}