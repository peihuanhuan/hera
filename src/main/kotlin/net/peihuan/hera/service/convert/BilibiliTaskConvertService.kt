package net.peihuan.hera.service.convert

import net.peihuan.hera.domain.BilibiliSubTask
import net.peihuan.hera.domain.BilibiliTask
import net.peihuan.hera.persistent.po.BilibiliSubTaskPO
import net.peihuan.hera.persistent.po.BilibiliTaskPO
import net.peihuan.hera.util.copyPropertiesTo
import org.springframework.stereotype.Component

@Component
class BilibiliTaskConvertService() {
    fun convert2BilibiliSubTask(bilibiliSubTaskPO: BilibiliSubTaskPO): BilibiliSubTask {
        return bilibiliSubTaskPO.copyPropertiesTo()
    }

    fun convert2BilibiliTask(bilibiliTaskPO: BilibiliTaskPO): BilibiliTask {
        return bilibiliTaskPO.copyPropertiesTo()
    }

    fun convert2BilibiliSubTaskPO(bilibiliSubTask: BilibiliSubTask): BilibiliSubTaskPO {
        return BilibiliSubTaskPO(
            taskId = bilibiliSubTask.taskId!!,
            openid = bilibiliSubTask.openid,
            title = bilibiliSubTask.bilibiliVideo.title!!,
            aid = bilibiliSubTask.bilibiliVideo.aid!!,
            bvid = bilibiliSubTask.bilibiliVideo.bvid,
            cid = bilibiliSubTask.bilibiliVideo.cid!!,
            mid = bilibiliSubTask.bilibiliVideo.mid ?: ""
        )
    }

    fun convert2BilibiliTaskPO(bilibiliTask: BilibiliTask): BilibiliTaskPO {
        return BilibiliTaskPO(
            openid = bilibiliTask.openid,
            request = bilibiliTask.request,
            name = bilibiliTask.name ?: "",
            type = bilibiliTask.type.code,
            notifyType = bilibiliTask.notifyType,
            size = bilibiliTask.subTaskSize,
            status = bilibiliTask.status.code,
            url = bilibiliTask.result ?: ""
        )
    }
}