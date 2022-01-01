package net.peihuan.hera.service

import mu.KotlinLogging
import net.peihuan.hera.constants.BilibiliTaskTypeEnum
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.domain.BilibiliVideo
import net.peihuan.hera.domain.CacheManage
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
    private val cacheManage: CacheManage,
    private val aliyundriveService: AliyundriveService,
    private val bilibiliAudioTaskPOService: BilibiliAudioTaskPOService,
    private val bilibiliAudioPOService: BilibiliAudioPOService,
    private val storageService: StorageService,
) {

    private val log = KotlinLogging.logger {}

    @Value("\${bilibili.workdir}")
    private val workDir: String? = null

    @Transactional
    fun saveTask2DB(data: String, type: Int): Int {
        val videos = bilibiliService.resolve2BilibiliVideos(data)

        if (videos.isEmpty()) {
            throw BizException.buildBizException("没解析到B站视频~")
        }
        if (type == BilibiliTaskTypeEnum.FREE.code) {
            return freeType(videos, data, type)
        }
        if (type == BilibiliTaskTypeEnum.MULTIPLE.code) {
            return multipleP(videos.first(), data, type)
        }
        return 0
    }

    private fun multipleP(video: BilibiliVideo, data: String, type: Int): Int {

        val view = bilibiliService.getViewByBvid(video.bvid)

        val limit = cacheManage.getBizValue(BizConfigEnum.MAX_P_LIMIT, "30").toInt()
        if (view.pages.size > limit) {
            throw BizException.buildBizException("不支持 P 数大于 $limit，联系公众号解决~")
        }

        val task = BilibiliAudioTaskPO(
            name = "",
            url = "",
            request = data,
            type = type,
            openid = currentUserOpenid,
            status = TaskStatusEnum.DEFAULT.code,
            size = view.pages.size
        )
        bilibiliAudioTaskPOService.save(task)


        val tasks = view.pages.map {
            BilibiliAudioPO(
                taskId = task.id!!,
                openid = task.openid,
                bvid = view.bvid,
                aid = view.aid,
                cid = it.cid,
                mid = view.owner.mid,
                title = it.part
            )
        }
        bilibiliAudioPOService.saveBatch(tasks)
        return tasks.size
    }

    private fun freeType(videos: List<BilibiliVideo>, data: String, type: Int): Int {

        val limit = cacheManage.getBizValue(BizConfigEnum.MAX_FREE_LIMIT, "10").toInt()
        if (videos.size > limit) {
            throw BizException.buildBizException("一次不能超过 $limit 个视频")
        }


        val task = BilibiliAudioTaskPO(
            name = "",
            url = "",
            request = data,
            type = type,
            openid = currentUserOpenid,
            status = TaskStatusEnum.DEFAULT.code,
            size = videos.size
        )
        bilibiliAudioTaskPOService.save(task)

        val views = videos.associateWith { bilibiliService.getViewByBvid(it.bvid) }

        val taskAudios = views.map {
            val pageNo = it.key.page
            val cid: String
            val title: String
            if (pageNo == null) {
                cid = it.value.cid
                title = it.value.title
            } else {
                val pageVideo = it.value.pages.filter { page -> page.page.toString() == pageNo }.first()
                cid = pageVideo.cid
                title = "${it.value.title} p$pageNo ${pageVideo.part}"
            }
            BilibiliAudioPO(
                taskId = task.id!!,
                openid = task.openid,
                bvid = it.key.bvid,
                aid = it.value.aid,
                cid = cid,
                mid = it.value.owner.mid,
                title = title
            )
        }
        bilibiliAudioPOService.saveBatch(taskAudios)
        return videos.size
    }


    @Scheduled(fixedDelay = 60_000)
    fun autoProcess() {
        val tasks = bilibiliAudioTaskPOService.findByStatus(TaskStatusEnum.DEFAULT)
        tasks.forEach {
            try {
                processVideos(it)
            } catch (e: Exception) {
                log.error(e.message, e)
                it.status = TaskStatusEnum.FAIL.code
                it.updateTime = null
                bilibiliAudioTaskPOService.updateById(it)
            }
        }
    }


    fun processVideos(task: BilibiliAudioTaskPO): String {

        val audios = bilibiliAudioPOService.findByTaskId(task.id!!)

        val audioFiles = audios.map { processBV(it) }
        val targetFile: File
        if (audios.size == 1) {
            targetFile = audioFiles[0]
        } else {
            val name = if (task.type == BilibiliTaskTypeEnum.MULTIPLE.code) {
                bilibiliService.getViewByBvid(audios.first().bvid).title
            } else {
                "「${audioFiles[0].name}」等${audioFiles.size}个文件"
            }
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
        task.updateTime = null
        bilibiliAudioTaskPOService.updateById(task)

        notifyService.notifyTaskResult(task)

        audioFiles.forEach {
            FileUtils.deleteQuietly(it)
        }

        return downloadUrl
    }

    fun processBV(bilibiliAudioPO: BilibiliAudioPO): File {

        var url = bilibiliService.getDashAudioPlayUrl(bilibiliAudioPO.aid, bilibiliAudioPO.cid)
        if (url == null) {
            url = bilibiliService.getFlvPlayUrl(bilibiliAudioPO.aid, bilibiliAudioPO.cid)!!
        }

        val bvId = bilibiliAudioPO.bvid
        val headers = mapOf(
            "Accept" to "*/*",
            "Accept-Encoding" to "gzip, deflate, br",
            "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7",
            "Connection" to "keep-alive",
            "origin" to "https://www.bilibili.com",
            "referer" to "https://www.bilibili.com/video/$bvId",
            "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
        )

        val source = "${workDir}/${bilibiliAudioPO.cid}.m4s"
        log.info { "====== 开始下载 $source" }
        doDownload(url, File(source), headers)

        val target = "${workDir}/${bilibiliAudioPO.cid}.mp3"
        FileUtils.deleteQuietly(File(target))
        log.info { "====== 开始转 $target" }
        CmdUtil.executeBash("ffmpeg -i $source $target")
        log.info { "====== 转换完成" }


        val title = bilibiliAudioPO.title.replace("/", "")
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