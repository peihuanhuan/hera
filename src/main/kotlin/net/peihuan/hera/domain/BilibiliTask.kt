package net.peihuan.hera.domain

import net.peihuan.hera.constants.BilibiliTaskTypeEnum
import net.peihuan.hera.constants.NotifyTypeEnum
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.exception.BizException

class BilibiliTask(
    var openid: String,
    var request: String,
    val type: BilibiliTaskTypeEnum,
    val notifyType: NotifyTypeEnum,
    var id: Long ?= null,


    var status: TaskStatusEnum = TaskStatusEnum.DEFAULT,
    var result: String? = null,
    var name: String? = null
) {

    val subTasks = mutableListOf<BilibiliSubTask>()

    private val totalDurationMinutes: Int
        get() = subTasks.sumOf { it.duration } / 60

    val subTaskSize: Int
        get() = subTasks.size

    fun addSubTask(task: BilibiliSubTask) {
        subTasks.add(task)
    }

    fun setId(id: Long) {
        this.id = id
        subTasks.forEach {
            it.taskId = id;
        }
    }

    fun validTask(freeLimit: Int, multiPLimit: Int, allowMaxDurationMinutes: Int) {
        if (totalDurationMinutes > allowMaxDurationMinutes) {
            throw BizException.buildBizException("视频总时长不能超过 $allowMaxDurationMinutes 分钟，如有需要请联系群主")
        }
        if (type == BilibiliTaskTypeEnum.FREE) {
            if (subTasks.size > freeLimit) {
                throw BizException.buildBizException("一次不能超过 $freeLimit 个视频，如有需要请联系群主")
            }
        } else {
            if (subTasks.size > multiPLimit) {
                throw BizException.buildBizException("不支持 P 数大于  $multiPLimit，如有需要请联系群主")
            }
        }
    }
}