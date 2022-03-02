package net.peihuan.hera.service

import mu.KotlinLogging
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.peihuan.hera.constants.BilibiliTaskTypeEnum
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.constants.NotifyTypeEnum
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.domain.BilibiliVideo
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.persistent.po.BilibiliAudioPO
import net.peihuan.hera.persistent.po.BilibiliAudioTaskPO
import net.peihuan.hera.persistent.service.BilibiliAudioPOService
import net.peihuan.hera.persistent.service.BilibiliAudioTaskPOService
import net.peihuan.hera.service.storage.StorageService
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Service
class BVideo2AudioService(
    private val bilibiliService: BilibiliService,
    private val notifyService: NotifyService,
    private val cacheManage: CacheManage,
    private val aliyundriveService: AliyundriveService,
    private val bilibiliAudioTaskPOService: BilibiliAudioTaskPOService,
    private val bilibiliAudioPOService: BilibiliAudioPOService,
    private val storageService: StorageService,
    private val grayService: GrayService,
    private val blackKeywordService: BlackKeywordService
) {

    private val log = KotlinLogging.logger {}
    private val executorService = Executors.newFixedThreadPool(3)

    private val processingTasks = mutableSetOf<Long>()

    @Value("\${bilibili.workdir}")
    private val workDir: String? = null

    @Transactional
    fun saveTask2DB(data: String, type: Int, notifyType: NotifyTypeEnum): Int {
        val videos = bilibiliService.resolve2BilibiliVideos(data)
        val notHandleTask = bilibiliAudioTaskPOService.findByOpenidAndStatus(currentUserOpenid, TaskStatusEnum.DEFAULT)
        if (notHandleTask.isNotEmpty()) {
            throw BizException.buildBizException("请等待上一个任务执行完成~")
        }

        if (videos.isEmpty()) {
            throw BizException.buildBizException("没解析到B站视频~")
        }
        if (type == BilibiliTaskTypeEnum.FREE.code) {
            return freeType(videos, data, type, notifyType)
        }
        if (type == BilibiliTaskTypeEnum.MULTIPLE.code) {
            return multipleP(videos.first(), data, type, notifyType)
        }
        return 0
    }

    private fun multipleP(video: BilibiliVideo, data: String, type: Int, notifyType: NotifyTypeEnum,): Int {

        val limit = cacheManage.getBizValue(BizConfigEnum.MAX_P_LIMIT, "35").toInt()

        if (video.epid != null) {
            val bangumiInfo = bilibiliService.getAllBangumiInfos(video.epid)
            if (bangumiInfo.size > limit) {
                throw BizException.buildBizException("不支持 P 数大于 $limit，联系群主/公众号解决~")
            }
            val task = BilibiliAudioTaskPO(
                name = "",
                url = "",
                request = data,
                type = type,
                notifyType = notifyType,
                openid = currentUserOpenid,
                status = TaskStatusEnum.DEFAULT.code,
                size = bangumiInfo.size
            )
            bilibiliAudioTaskPOService.save(task)

            val tasks = bangumiInfo.map {
                BilibiliAudioPO(
                    taskId = task.id!!,
                    openid = task.openid,
                    bvid = it.bvid,
                    aid = it.aid!!,
                    cid = it.cid!!,
                    mid = "",
                    title = it.title!!
                )
            }
            bilibiliAudioPOService.saveBatch(tasks)
            return task.size
        } else {
            val view = bilibiliService.getViewByBvid(video.bvid)

            if (view.pages.size > limit) {
                throw BizException.buildBizException("不支持 P 数大于 $limit，联系群主/公众号解决~")
            }

            val task = BilibiliAudioTaskPO(
                name = "",
                url = "",
                request = data,
                type = type,
                notifyType = notifyType,
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





    }

    private fun freeType(videos: List<BilibiliVideo>, data: String, type: Int, notifyType: NotifyTypeEnum): Int {
        val limit = cacheManage.getBizValue(BizConfigEnum.MAX_FREE_LIMIT, "10").toInt()
        if (videos.size > limit) {
            throw BizException.buildBizException("一次不能超过 $limit 个视频")
        }
        val views = videos.associateWith { bilibiliService.getViewByBvid(it.bvid) }

        var totalDuration = 0
        views.values.forEach { view ->
            totalDuration += view.duration
        }
        val maxDurationMinute = cacheManage.getBizValue(BizConfigEnum.MAX_DURATION_MINUTE, "300").toInt()
        if (totalDuration > maxDurationMinute * 60) {
            throw BizException.buildBizException("视频总时长不能超过 $maxDurationMinute 分钟")
        }

        val task = BilibiliAudioTaskPO(
            name = "",
            url = "",
            request = data,
            type = type,
            notifyType = notifyType,
            openid = currentUserOpenid,
            status = TaskStatusEnum.DEFAULT.code,
            size = videos.size
        )
        bilibiliAudioTaskPOService.save(task)


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


    @Scheduled(fixedDelay = 30_000)
    fun autoProcess() {
        val tasks = bilibiliAudioTaskPOService.findByStatus(TaskStatusEnum.DEFAULT)
        tasks.forEach { task ->
            if (processingTasks.contains(task.id!!)) {
                return@forEach
            }
            processingTasks.add(task.id!!)
            log.info { "新增任务 ${task.request}" }
            executorService.execute {
                processVideos(task)
                processingTasks.remove(task.id)
            }
        }
    }

    fun processVideos(task: BilibiliAudioTaskPO): String {
        try {
            val byId = bilibiliAudioTaskPOService.getById(task.id!!)
            if (byId.status == TaskStatusEnum.SUCCESS.code) {
                // 再次进行检查
                return byId.url
            }
            // 手动恢复
            if(byId.url.isNotBlank() && byId.name.isNotBlank()) {
                task.status = TaskStatusEnum.SUCCESS.code
                task.updateTime = null
                bilibiliAudioTaskPOService.updateById(task)
                notifyService.notifyTaskResult(task)
                return task.url
            }

            val subTasks = bilibiliAudioPOService.findByTaskId(task.id!!)


            if(grayService.isGrayUser(task.openid)) {

                val fileIds = subTasks.map { subTask ->

                    if (!subTask.fileId.isNullOrEmpty()) {
                        val fileDTO = aliyundriveService.get(subTask.fileId!!)
                        if (fileDTO != null && !fileDTO.trashed) {
                            return@map fileDTO.file_id
                        }
                    }

                    val targetFile = processBV(subTask, 3)
                    val createWithFoldersDTO = aliyundriveService.uploadFile(targetFile)

                    subTask.fileId = createWithFoldersDTO.file_id
                    subTask.updateTime = null
                    bilibiliAudioPOService.updateById(subTask)

                    FileUtils.deleteQuietly(targetFile)

                    createWithFoldersDTO.file_id
                }
                val share = aliyundriveService.share(fileIds)
                task.url = share.full_share_msg
                task.name = share.share_name

            } else {
                val audioFiles = subTasks.map { processBV(it, 3) }

                val targetFile: File
                if (subTasks.size == 1) {
                    targetFile = audioFiles[0]
                } else {
                    targetFile = zipFiles(task, subTasks, audioFiles)
                }

                val objectName = targetFile.name
                log.info { "开始上传文件 $objectName，大小：${FileUtils.sizeOf(targetFile) / (1024 * 1024)}M" }
                storageService.upload(objectName, targetFile.absolutePath)
                FileUtils.deleteQuietly(targetFile)
                val downloadUrl = storageService.getDownloadUrl(objectName)

                task.name = FilenameUtils.getBaseName(targetFile.name)
                task.url = downloadUrl

                audioFiles.forEach {
                    FileUtils.deleteQuietly(it)
                }
            }

            if (task.name.length >= 20) {
                task.name = task.name.substring(0, 8) + "..." + task.name.substring(task.name.length - 8)
            }

            task.status = TaskStatusEnum.SUCCESS.code
            task.updateTime = null
            bilibiliAudioTaskPOService.updateById(task)
            task.name = blackKeywordService.replaceBlackKeyword(task.name)
            notifyService.notifyTaskResult(task)



            return task.url
        } catch (e: Exception) {
            log.error(e.message, e)
            notifyService.notifyTaskFail(task)
            task.status = TaskStatusEnum.FAIL.code
            task.updateTime = null
            bilibiliAudioTaskPOService.updateById(task)
        }
        return "处理失败"
    }

    private fun zipFiles(
        task: BilibiliAudioTaskPO,
        audios: List<BilibiliAudioPO>,
        audioFiles: List<File>,
    ): File {
        val name = if (task.type == BilibiliTaskTypeEnum.MULTIPLE.code) {
            bilibiliService.getViewByBvid(audios.first().bvid).title
        } else {
            "「${audioFiles[0].name}」等${audioFiles.size}个文件"
        }.replace("/", "")
        val targetFile = File("${workDir}/$name.zip")
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
        return targetFile
    }

    fun processBV(bilibiliAudioPO: BilibiliAudioPO, tryTime: Int): File {
        var count = 0
        var file: File? = null
        while (file?.exists() != true && count++ < tryTime) {
            try {
                file = processBV(bilibiliAudioPO)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
        return file!!
    }

    fun processBV(bilibiliAudioPO: BilibiliAudioPO): File {

        var title = bilibiliAudioPO.title.replace("/", "")
        if (title.length > 40) {
            // linux 文件名最大 255 个字符，这边截取一部分
            title = title.substring(0, 20) + "..." + title.substring(title.length - 20)
        }

        val pathname = "${workDir}/${title}.mp3"
        val destentFile = File(pathname)
        if (destentFile.exists()) {
            log.info { "已存在 $pathname 直接使用" }
            return destentFile
        }

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
        doDownload(url, File(source), headers, 5)

        val target = "${workDir}/${bilibiliAudioPO.cid}.mp3"
        FileUtils.deleteQuietly(File(target))
        // CmdUtil.executeBash("ffmpeg -i $source $target")
        ffmpeg(source, target)

        File(target).renameTo(destentFile)

        FileUtils.deleteQuietly(File(target))
        FileUtils.deleteQuietly(File(source))
        return destentFile
    }

    fun ffmpeg(inputPath: String, outputPath: String) {
        val ffmpeg = FFmpeg("ffmpeg")
        val ffprobe = FFprobe("ffprobe")

        val inputProbe = ffprobe.probe(inputPath)

        // 根据不同的时长，设置合适的比特率
        val byteRate: Long = if (inputProbe.getFormat().duration < TimeUnit.MINUTES.toSeconds(10)) {
            192
        } else if (inputProbe.getFormat().duration < TimeUnit.MINUTES.toSeconds(100)){
            128
        } else if (inputProbe.getFormat().duration < TimeUnit.MINUTES.toSeconds(200)){
            64
        } else if (inputProbe.getFormat().duration < TimeUnit.MINUTES.toSeconds(400)) {
            32
        } else if (inputProbe.getFormat().duration < TimeUnit.MINUTES.toSeconds(800)) {
            16
        } else {
            16
        }

        val builder = FFmpegBuilder()
            .setInput(inputPath) // Filename, or a FFmpegProbeResult
            .overrideOutputFiles(true) // Override the output if it exists
            .addOutput(outputPath) // Filename for the destination
            .setAudioBitRate(byteRate * 1024) // at 32 kbit/s
            .done()

        val executor = FFmpegExecutor(ffmpeg, ffprobe)

        executor.createJob(builder) { progress ->
            // 转换进度 [0, 100]
            val duration_ns: Double = inputProbe.getFormat().duration * TimeUnit.SECONDS.toNanos(1)

            val percentage = if (duration_ns > 0) (progress.out_time_ns / duration_ns * 100) else 99

            log.info(
                "进度：[{}%] 耗时: {}",
                percentage.toInt(),
                FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
            )
        }.run()

    }

}