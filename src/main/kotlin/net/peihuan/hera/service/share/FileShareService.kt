package net.peihuan.hera.service.share

import net.peihuan.hera.domain.BilibiliSubTask
import net.peihuan.hera.domain.BilibiliTask

interface FileShareService {
    fun needConvertFiles(task: BilibiliTask): List<BilibiliSubTask>
    fun uploadAndAssembleTaskShare(task: BilibiliTask, needUpload: List<BilibiliSubTask>)
}