package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.domain.BilibiliTask
import net.peihuan.hera.persistent.mapper.BilibiliAudioTaskMapper
import net.peihuan.hera.persistent.po.BilibiliTaskPO
import net.peihuan.hera.service.convert.BilibiliTaskConvertService
import org.springframework.stereotype.Service

@Service
class BilibiliAudioTaskPOService(
    private val bilibiliAudioPOService: BilibiliAudioPOService,
    private val bilibiliTaskConvertService: BilibiliTaskConvertService
) : ServiceImpl<BilibiliAudioTaskMapper, BilibiliTaskPO>() {

    fun findByStatus(vararg statusEnum: TaskStatusEnum): List<BilibiliTaskPO> {
        return list(KtQueryWrapper(BilibiliTaskPO::class.java).`in`(BilibiliTaskPO::status, statusEnum))
    }

    fun getTask(id: Long): BilibiliTask {
        val taskPO = getById(id)
        val subTaskPOs = bilibiliAudioPOService.findByTaskId(taskPO.id!!)

        val task = bilibiliTaskConvertService.convert2BilibiliTask(taskPO)
        val subTasks = subTaskPOs.map { bilibiliTaskConvertService.convert2BilibiliSubTask(task, it) }
        task.addSubTasks(subTasks)
        return task
    }


    fun findByOpenidAndStatus(openid: String, statusEnum: TaskStatusEnum): List<BilibiliTaskPO> {
        return list(
            KtQueryWrapper(BilibiliTaskPO::class.java)
                .eq(BilibiliTaskPO::openid, openid)
                .eq(BilibiliTaskPO::status, statusEnum.code)
                .orderByDesc(BilibiliTaskPO::updateTime)
        )
    }

    fun findLastByOpenid(openid: String): BilibiliTaskPO? {
        return getOne(
            KtQueryWrapper(BilibiliTaskPO::class.java)
                .eq(BilibiliTaskPO::openid, openid)
                .orderByDesc(BilibiliTaskPO::updateTime)
                .last("limit 1")
        )
    }
}