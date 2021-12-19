package net.peihuan.hera.service

import mu.KotlinLogging
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.persistent.po.BilibiliAudioPO
import net.peihuan.hera.persistent.po.BilibiliAudioTaskPO
import net.peihuan.hera.persistent.service.BilibiliAudioPOService
import net.peihuan.hera.persistent.service.BilibiliAudioTaskPOService
import net.peihuan.hera.service.storage.StorageService
import net.peihuan.hera.util.CmdUtil
import net.peihuan.hera.util.currentUserOpenid
import net.peihuan.hera.util.doDownload
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileInputStream


@Service
class BVideo2AudioService(
    private val bilibiliService: BilibiliService,
    private val notifyService: NotifyService,
    private val aliyundriveService: AliyundriveService,
    private val bilibiliAudioTaskPOService: BilibiliAudioTaskPOService,
    private val bilibiliAudioPOService: BilibiliAudioPOService,
    private val storageService: StorageService,
) {

    private val log = KotlinLogging.logger {}

    @Value("\${bilibili.workdir}")
    private val workDir: String? = null

    @Transactional
    fun saveTask2DB(data: String): Int {
        val bvIds = bilibiliService.resolveBVids(data)

        if (bvIds.isEmpty()) {
            throw BizException.buildBizException("没解析到B站视频~")
        }

        if (bvIds.size > 5) {
            throw BizException.buildBizException("一次不能超过五个文件")
        }

        val task = BilibiliAudioTaskPO(
            name = "",
            url = "",
            request = data,
            type = 1,  //todo 类型写死了
            openid = currentUserOpenid,
            status = TaskStatusEnum.DEFAULT.code,
            size = bvIds.size
        )
        bilibiliAudioTaskPOService.save(task)

        val views = bvIds.map { bilibiliService.getViewByBvid(it) }
        val taskAudios = views.map {
            BilibiliAudioPO(
                taskId = task.id!!,
                openid = task.openid,
                bvid = it.bvid,
                aid = it.aid,
                mid = it.owner.mid,
                title = it.title
            )
        }
        bilibiliAudioPOService.saveBatch(taskAudios)
        return bvIds.size
    }


    @Scheduled(fixedDelay = 60_000)
    fun autoProcess() {
        val tasks = bilibiliAudioTaskPOService.findByStatus(TaskStatusEnum.DEFAULT)
        tasks.forEach {
            processVideos(it)
        }
    }


    fun processVideos(task: BilibiliAudioTaskPO): String {
        // task.status = TaskStatusEnum.PROCESS.code
        // bilibiliAudioTaskPOService.updateById(task)

        val audios = bilibiliAudioPOService.findByTaskId(task.id!!)

        val audioFiles = audios.map { processBV(it.bvid) }
        val targetFile: File
        val expireDays = 7
        if (audios.size == 1) {
            targetFile = audioFiles[0]
        } else {
            val name = "「${audioFiles[0].name}」等${audioFiles.size}个文件"
            targetFile = File("${workDir}/$name.zip")
            ZipArchiveOutputStream(targetFile).use { zipArchiveOutputStream ->
                audioFiles.forEach { file ->
                    val zipArchiveEntry = ZipArchiveEntry(file, name + File.separator + file.name)
                    zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry)
                    val inputStream = FileInputStream(file)
                    val buffer = ByteArray(1024 * 5)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        zipArchiveOutputStream.write(buffer, 0, len)
                    }
                }
                zipArchiveOutputStream.closeArchiveEntry()
                zipArchiveOutputStream.finish()
            }
        }


        val objectName = targetFile.name
        storageService.upload(objectName, targetFile.absolutePath)
        FileUtils.deleteQuietly(targetFile)

        val downloadUrl = storageService.getDownloadUrl(objectName)

        task.status = TaskStatusEnum.SUCCESS.code
        val baseName = FilenameUtils.getBaseName(targetFile.name)
        if (baseName.length < 20) {
            task.name = baseName
        } else {
            task.name = baseName.substring(0, 8) + "..." + baseName.substring(baseName.length - 8)
        }
        task.url = downloadUrl
        bilibiliAudioTaskPOService.updateById(task)

        notifyService.notifyTaskResult(task)

        audioFiles.forEach {
            FileUtils.deleteQuietly(it)
        }

        return downloadUrl
    }

    fun processBV(bvId: String): File {

        val viewByBvid = bilibiliService.getViewByBvid(bvId)
        val dashAudioPlayUrl =
            bilibiliService.getDashAudioPlayUrl(viewByBvid.aid, viewByBvid.cid) ?: throw BizException.buildBizException(
                "未解析到音频"
            )

        val headers = mapOf(
            "Accept" to "*/*",
            "Accept-Encoding" to "gzip, deflate, br",
            "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7",
            "Connection" to "keep-alive",
            "origin" to "https://www.bilibili.com",
            "referer" to "https://www.bilibili.com/video/$bvId",
            "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
        )

        val source = "${workDir}/${bvId}.m4s"
        log.info { "====== 开始下载 $source" }
        doDownload(dashAudioPlayUrl, File(source), headers)

        val target = "${workDir}/${bvId}.mp3"
        FileUtils.deleteQuietly(File(target))
        log.info { "====== 开始转 $target" }
        CmdUtil.executeBash("ffmpeg -i $source $target")
        log.info { "====== 转换完成" }


        val title = viewByBvid.title.replace("/", "")
        val pathname = "${workDir}/${title}.mp3"
        val renameFile = File(pathname)
        File(target).renameTo(renameFile)
        // val createWithFoldersDTO = aliyundriveService.uploadFile(renameFile)
        // val share = aliyundriveService.share(createWithFoldersDTO.file_id)

        FileUtils.deleteQuietly(File(target))
        FileUtils.deleteQuietly(File(source))
        return renameFile
    }

}