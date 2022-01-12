package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.BilibiliAudioMapper
import net.peihuan.hera.persistent.po.BilibiliAudioPO
import org.springframework.stereotype.Service

@Service
class BilibiliAudioPOService : ServiceImpl<BilibiliAudioMapper, BilibiliAudioPO>() {

    fun findByTaskId(taskId: Long): List<BilibiliAudioPO> {
        return list(KtQueryWrapper(BilibiliAudioPO::class.java).eq(BilibiliAudioPO::taskId, taskId))
    }


}