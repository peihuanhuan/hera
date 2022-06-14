package net.peihuan.hera.service.convert

import net.peihuan.hera.constants.BilibiliTaskSourceTypeEnum
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.domain.BilibiliSubTask
import net.peihuan.hera.domain.BilibiliTask
import net.peihuan.hera.domain.BilibiliVideo
import net.peihuan.hera.persistent.po.BilibiliSubTaskPO
import net.peihuan.hera.persistent.po.BilibiliTaskPO
import org.springframework.stereotype.Component

@Component
class BilibiliTaskConvertService() {

    fun convert2BilibiliSubTask(task: BilibiliTask, po: BilibiliSubTaskPO): BilibiliSubTask {
        val bilibiliVideo = BilibiliVideo(
            title = po.title,
            aid = po.aid,
            bvid = po.bvid,
            cid = po.cid,
            sid = po.sid,
            duration = po.duration,
            mid = po.mid
        )
        return BilibiliSubTask(
            taskId = po.taskId,
            openid = po.openid,
            id = po.id,
            bilibiliVideo = bilibiliVideo,
            aliyundriverFileId = po.fileId,
            baiduPanFileId = po.baiduFileId,
            parentTask = task
        )
    }

    fun convert2BilibiliTask(po: BilibiliTaskPO): BilibiliTask {
        return BilibiliTask(
            id = po.id,
            openid = po.openid,
            request = po.request,
            name = po.name,
            type = BilibiliTaskSourceTypeEnum.getTypeEnum(po.type)!!,
            notifyType = po.notifyType,
            status = TaskStatusEnum.getTypeEnum(po.status)!!,
            result = po.url,
            outputType = po.outputType
        )
    }

    fun convert2BilibiliSubTaskPO(bilibiliSubTask: BilibiliSubTask): BilibiliSubTaskPO {
        return BilibiliSubTaskPO(
            id = bilibiliSubTask.id,
            taskId = bilibiliSubTask.taskId!!,
            openid = bilibiliSubTask.openid,
            title = bilibiliSubTask.originalTitle,
            aid = bilibiliSubTask.aid,
            bvid = bilibiliSubTask.bvid,
            sid = bilibiliSubTask.sid,
            cid = bilibiliSubTask.cid,
            duration = bilibiliSubTask.duration,
            mid = bilibiliSubTask.mid,
            fileId = bilibiliSubTask.aliyundriverFileId,
            baiduFileId = bilibiliSubTask.baiduPanFileId
        )
    }

    fun convert2BilibiliTaskPO(bilibiliTask: BilibiliTask): BilibiliTaskPO {
        return BilibiliTaskPO(
            id = bilibiliTask.id,
            openid = bilibiliTask.openid,
            request = bilibiliTask.request,
            name = bilibiliTask.name ?: "",
            type = bilibiliTask.type.code,
            notifyType = bilibiliTask.notifyType,
            size = bilibiliTask.subTaskSize,
            status = bilibiliTask.status.code,
            url = bilibiliTask.result,
            outputType = bilibiliTask.outputType
        )
    }
}