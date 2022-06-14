package net.peihuan.hera.service.share

import net.peihuan.baiduPanSDK.service.BaiduService
import net.peihuan.baiduPanSDK.service.impl.BaiduOAuthConfigServiceImpl
import net.peihuan.hera.config.WxMysqlOps
import net.peihuan.hera.constants.BilibiliTaskSourceTypeEnum
import net.peihuan.hera.domain.BilibiliSubTask
import net.peihuan.hera.domain.BilibiliTask
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.persistent.service.BilibiliAudioPOService
import net.peihuan.hera.persistent.service.LockPOService
import net.peihuan.hera.service.BlackKeywordService
import net.peihuan.hera.service.ConfigService
import net.peihuan.hera.service.NotifyService
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class BaiduPanService(
    private val notifyService: NotifyService,
    private val baiduService: BaiduService,
    private val blackKeywordService: BlackKeywordService,
    private val cacheManage: CacheManage,
    private val configService: ConfigService,
    private val lockPOService: LockPOService,
    private val bilibiliAudioPOService: BilibiliAudioPOService,

    ): FileShareService {


    companion object val ROOT_USER_ID = "1"

    @PostConstruct
    fun setToken() {
        val wxMysqlOps = WxMysqlOps(configService, lockPOService)
        val storage = BaiduOAuthConfigServiceImpl(wxMysqlOps)
        baiduService.setConfigStorage(storage)
    }

    override fun needConvertFiles(task: BilibiliTask): List<BilibiliSubTask> {
        val success =  task.subTasks.filter {
            if (it.baiduPanFileId == null) {
                return@filter false
            }
            val filemetas = baiduService.getPanService().filemetas(ROOT_USER_ID, listOf(it.baiduPanFileId!!))
            if (filemetas.isEmpty()) {
                return@filter false
            }
            return@filter true
        }
        return task.subTasks.filter { !success.contains(it) }
    }

    override fun uploadAndAssembleTaskShare(task: BilibiliTask, needUpload: List<BilibiliSubTask>) {
        val rootPath = if (task.type == BilibiliTaskSourceTypeEnum.MULTIPLE) {
            "${task.openid}/${task.name}/"
        } else {
            "/"
        }

        needUpload.forEach { subTask ->
            val resp = baiduService.getPanService().uploadFile(ROOT_USER_ID, rootPath + subTask.outFile!!.name, subTask.outFile!!)
            subTask.baiduPanFileId = resp.fs_id
            bilibiliAudioPOService.updateSubTask(subTask)
        }

        val shareFileIds: List<Long> = task.subTasks.map { it.baiduPanFileId!! }

        val shareResp = baiduService.getPanService().shareFiles(ROOT_USER_ID, shareFileIds, 1, "欢迎关注阿烫")
        task.result = "百度云盘链接 ${shareResp.link} 提取码 ${shareResp.pwd}"
    }


}
