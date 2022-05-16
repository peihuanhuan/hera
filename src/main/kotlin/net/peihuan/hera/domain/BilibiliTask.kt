package net.peihuan.hera.domain

import mu.KotlinLogging
import net.peihuan.hera.constants.BilibiliTaskOutputTypeEnum
import net.peihuan.hera.constants.BilibiliTaskSourceTypeEnum
import net.peihuan.hera.constants.NotifyTypeEnum
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.exception.BizException

private val log = KotlinLogging.logger {}

class BilibiliTask(
    var id: Long ?= null,
    var openid: String,
    var request: String,
    val type: BilibiliTaskSourceTypeEnum,
    var outputType: BilibiliTaskOutputTypeEnum,
    val notifyType: NotifyTypeEnum,

    var status: TaskStatusEnum = TaskStatusEnum.DEFAULT,
    var result: String = "",
    var name: String? = null
) {

    val subTasks = mutableListOf<BilibiliSubTask>()

    private val totalDurationMinutes: Int
        get() = subTasks.sumOf { it.duration } / 60

    val subTaskSize: Int
        get() = subTasks.size

    // 消息模板最多20个字符
    fun trimName() {
        if (name!!.length >= 20) {
            name = name!!.substring(0, 8) + "..." + name!!.substring(name!!.length - 8)
        }
    }

    fun addSubTasks(tasks: List<BilibiliSubTask>) {
        subTasks.addAll(tasks)
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
        val videoLimit = 45
        if (isVideoOutputTask() && totalDurationMinutes > videoLimit) {
            throw BizException.buildBizException("【提取完整视频】暂时限制总时长不能超过 $videoLimit 分钟")
        }
        if (type == BilibiliTaskSourceTypeEnum.FREE) {
            if (subTasks.size > freeLimit) {
                throw BizException.buildBizException("一次不能超过 $freeLimit 个视频，如有需要请联系群主")
            }
        } else {
            if (subTasks.size > multiPLimit) {
                throw BizException.buildBizException("不支持 P 数大于  $multiPLimit，如有需要请联系群主")
            }
        }
    }

    fun isVideoOutputTask(): Boolean {
        return outputType == BilibiliTaskOutputTypeEnum.VIDEO
    }
}