package net.peihuan.hera.service.share

import net.peihuan.hera.domain.BilibiliSubTask
import net.peihuan.hera.domain.BilibiliTask

interface FileShareService {
    fun needReConvert(subTask: BilibiliSubTask): Boolean
    fun uploadAndAssembleTaskShare(
        task: BilibiliTask,
        convert: (subTask: BilibiliSubTask) -> Unit
    )
}