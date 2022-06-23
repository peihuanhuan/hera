package net.peihuan.hera.service.share

import mu.KotlinLogging
import net.peihuan.baiduPanSDK.domain.dto.RtypeEnum
import net.peihuan.baiduPanSDK.service.BaiduService
import net.peihuan.baiduPanSDK.service.impl.BaiduOAuthConfigServiceImpl
import net.peihuan.hera.config.WxMysqlOps
import net.peihuan.hera.constants.BilibiliTaskSourceTypeEnum
import net.peihuan.hera.domain.BilibiliSubTask
import net.peihuan.hera.domain.BilibiliTask
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.persistent.service.BilibiliAudioPOService
import net.peihuan.hera.persistent.service.LockPOService
import net.peihuan.hera.service.BlackKeywordService
import net.peihuan.hera.service.ConfigService
import net.peihuan.hera.service.NotifyService
import net.peihuan.hera.util.blockWithTry
import org.apache.commons.io.FilenameUtils
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

    ) : FileShareService {


    private val log = KotlinLogging.logger {}

    companion object

    val ROOT_USER_ID = "1"

    @PostConstruct
    fun setToken() {
        val wxMysqlOps = WxMysqlOps(configService, lockPOService)
        val storage = BaiduOAuthConfigServiceImpl(wxMysqlOps)
        baiduService.setConfigStorage(storage)
    }

    override fun needReConvert(it: BilibiliSubTask): Boolean {
        if (it.baiduPanFileId == null) {
            return true
        }
        val filemetas = baiduService.getPanService().filemetas(ROOT_USER_ID, listOf(it.baiduPanFileId!!))
        if (filemetas.isEmpty()) {
            return true
        }
        return false
    }

    fun String.trimName(): String {
        // 百度云盘上传时需要剔除的字符 ?|\"><:*
        return this
            .replace("/", "")
            .replace("\"", "")
            .replace("?", "")
            .replace("|", "")
            .replace(">", "")
            .replace("<", "")
            .replace(":", "")
            .replace("*", "")
    }

    override fun uploadAndAssembleTaskShare(
        task: BilibiliTask,
        convert: (subTask: BilibiliSubTask) -> Unit
    ) {
        val rootPath = if (task.type == BilibiliTaskSourceTypeEnum.MULTIPLE) {
            "${task.openid}/${task.name!!.trimName()}/"
        } else {
            "/"
        }

        task.subTasks.forEach { subTask ->
            if (needReConvert(subTask))  {
                convert(subTask)
                // 去除特殊字符
                val path = subTask.outFile!!.name.trimName()
                val resp = blockWithTry(retryTime = 5) {
                    log.info("开始上传百度云盘 {}", rootPath + path)
                    baiduService.getPanService()
                        .uploadFile(ROOT_USER_ID, rootPath + path, subTask.outFile!!, rtype = RtypeEnum.OVERRIDE)
                }
                log.info("上传结束 {}", resp)
                subTask.baiduPanFileId = resp.fs_id
                bilibiliAudioPOService.updateSubTask(subTask)
            }

        }

        // 百度网盘禁止分享文件夹
        val shareFiles = task.subTasks.map { it.baiduPanFileId!! }


        val shareResp =
            blockWithTry(5) { baiduService.getPanService().shareFiles(ROOT_USER_ID, shareFiles, 1, "欢迎关注阿烫") }

        if (shareResp.errno != 0) {
            log.error("分享失败 {}", shareResp)
            throw BizException.buildBizException("百度云盘分享失败")
        }

        task.result = "hi，这是我用百度网盘分享的内容~复制这段内容打开「百度网盘」APP即可获取 \n" +
                "链接:${shareResp.link}\n\n提取码:${shareResp.pwd}"

        if (task.type == BilibiliTaskSourceTypeEnum.FREE) {
            task.name =
                "【" + FilenameUtils.getBaseName(task.subTasks.first().outFile!!.name) + "】等${task.subTaskSize}个文件"
        }
    }


}
