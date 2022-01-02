package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.persistent.mapper.BilibiliAudioTaskMapper
import net.peihuan.hera.persistent.po.BilibiliAudioTaskPO
import org.springframework.stereotype.Service

@Service
class BilibiliAudioTaskPOService : ServiceImpl<BilibiliAudioTaskMapper, BilibiliAudioTaskPO>() {

    fun findByStatus(vararg statusEnum: TaskStatusEnum): List<BilibiliAudioTaskPO> {
        return list(KtQueryWrapper(BilibiliAudioTaskPO::class.java).`in`(BilibiliAudioTaskPO::status, statusEnum))
    }

    fun findByOpenidAndStatus(openid: String, statusEnum: TaskStatusEnum): List<BilibiliAudioTaskPO> {
        return list(KtQueryWrapper(BilibiliAudioTaskPO::class.java)
            .eq(BilibiliAudioTaskPO::openid, openid)
            .eq(BilibiliAudioTaskPO::status, statusEnum.code)
            .orderByDesc(BilibiliAudioTaskPO::updateTime)
        )
    }
}