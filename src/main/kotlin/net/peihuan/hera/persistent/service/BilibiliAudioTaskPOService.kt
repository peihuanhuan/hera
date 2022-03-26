package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.persistent.mapper.BilibiliAudioTaskMapper
import net.peihuan.hera.persistent.po.BilibiliTaskPO
import org.springframework.stereotype.Service

@Service
class BilibiliAudioTaskPOService : ServiceImpl<BilibiliAudioTaskMapper, BilibiliTaskPO>() {

    fun findByStatus(vararg statusEnum: TaskStatusEnum): List<BilibiliTaskPO> {
        return list(KtQueryWrapper(BilibiliTaskPO::class.java).`in`(BilibiliTaskPO::status, statusEnum))
    }

    fun findByOpenidAndStatus(openid: String, statusEnum: TaskStatusEnum): List<BilibiliTaskPO> {
        return list(KtQueryWrapper(BilibiliTaskPO::class.java)
            .eq(BilibiliTaskPO::openid, openid)
            .eq(BilibiliTaskPO::status, statusEnum.code)
            .orderByDesc(BilibiliTaskPO::updateTime)
        )
    }
}