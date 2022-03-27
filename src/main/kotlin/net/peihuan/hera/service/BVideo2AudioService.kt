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
import net.peihuan.hera.domain.BilibiliSubTask
import net.peihuan.hera.domain.BilibiliTask
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.persistent.po.BilibiliTaskPO
import net.peihuan.hera.persistent.service.BilibiliAudioPOService
import net.peihuan.hera.persistent.service.BilibiliAudioTaskPOService
import net.peihuan.hera.service.AliyundriveService.Companion.DEFAULT_ROOT_ID
import net.peihuan.hera.service.convert.BilibiliTaskConvertService
import net.peihuan.hera.service.storage.StorageService
import net.peihuan.hera.util.currentUserOpenid
import net.peihuan.hera.util.doDownloadBilibiliVideo
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
    private val bilibiliTaskConvertService: BilibiliTaskConvertService,
    private val grayService: GrayService,
    private val blackKeywordService: BlackKeywordService
) {

    private val log = KotlinLogging.logger {}
    private val executorService = Executors.newFixedThreadPool(5)

    private val processingTasks = mutableSetOf<Long>()

    @Value("\${bilibili.workdir}")
    private val workDir: String? = null


    @Transactional
    fun saveTask(data: String, type: BilibiliTaskTypeEnum, notifyType: NotifyTypeEnum): Int {

        val notHandleTask = bilibiliAudioTaskPOService.findByOpenidAndStatus(currentUserOpenid, TaskStatusEnum.DEFAULT)
        if (notHandleTask.isNotEmpty()) {
            throw BizException.buildBizException("请等待上一个任务执行完成~")
        }

        val task: BilibiliTask = generateBilibiliTask(data, type, notifyType)

        val freeLimit = cacheManage.getBizValue(BizConfigEnum.MAX_FREE_LIMIT, "10").toInt()
        val multiPLimit = cacheManage.getBizValue(BizConfigEnum.MAX_P_LIMIT, "35").toInt()
        val maxDurationMinute = cacheManage.getBizValue(BizConfigEnum.MAX_DURATION_MINUTE, "300").toInt()

        task.validTask(freeLimit = freeLimit, multiPLimit = multiPLimit, allowMaxDurationMinutes = maxDurationMinute)

        val taskPO: BilibiliTaskPO = bilibiliTaskConvertService.convert2BilibiliTaskPO(task)
        bilibiliAudioTaskPOService.save(taskPO)
        task.setId(taskPO.id!!)
        val subTaskPOs = task.subTasks.map { bilibiliTaskConvertService.convert2BilibiliSubTaskPO(it) }
        bilibiliAudioPOService.saveBatch(subTaskPOs)

        return task.subTaskSize
    }

    fun generateBilibiliTask(
        requestData: String,
        type: BilibiliTaskTypeEnum,
        notifyType: NotifyTypeEnum
    ): BilibiliTask {
        var bilibiliVideos = bilibiliService.resolve2BilibiliVideos(requestData)
        if (bilibiliVideos.isEmpty()) {
            throw BizException.buildBizException("没解析到B站视频~")
        }

        var taskName = ""
        if (type == BilibiliTaskTypeEnum.MULTIPLE) {
            val firstVideo = bilibiliVideos.first()
            bilibiliVideos = bilibiliService.findAllBilibiliVideos(firstVideo)
            taskName = bilibiliService.getViewByBvid(firstVideo.bvid).title
        }

        val task = BilibiliTask(
            request = requestData,
            name = taskName,
            type = type,
            notifyType = notifyType,
            openid = currentUserOpenid
        )
        val subTasks = bilibiliVideos.map { BilibiliSubTask(bilibiliVideo = it, openid = currentUserOpenid) }
        task.addSubTasks(subTasks)
        return task
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
                handleTask(task)
                processingTasks.remove(task.id)
            }
        }
    }


    fun handleTask(taskPO: BilibiliTaskPO): String {
        // 检查是否有手动添加结果
        val manualResult = checkTaskHasManualResult(taskPO.id!!)
        if (manualResult.isNotBlank()) {
            return manualResult
        }

        val subTaskPOs = bilibiliAudioPOService.findByTaskId(taskPO.id!!)

        val task = bilibiliTaskConvertService.convert2BilibiliTask(taskPO)
        val subTasks = subTaskPOs.map { bilibiliTaskConvertService.convert2BilibiliSubTask(it) }
        task.addSubTasks(subTasks)

        return handleTask(task)
    }


    private fun findAliyunDriverSuccessSubTask(task: BilibiliTask): List<BilibiliSubTask> {
        return task.subTasks.filter {
            if (it.aliyundriverFileId.isNullOrEmpty()) {
                return@filter false
            }
            return@filter aliyundriveService.checkFileExisted(it.aliyundriverFileId!!)
        }
    }


    private fun updateSubTask(subTask: BilibiliSubTask) {
        val po = bilibiliTaskConvertService.convert2BilibiliSubTaskPO(subTask)
        bilibiliAudioPOService.updateById(po)
    }

    private fun updateTaskStatus(task: BilibiliTask, statusEnum: TaskStatusEnum) {
        val po = bilibiliTaskConvertService.convert2BilibiliTaskPO(task)
        po.status = statusEnum.code
        bilibiliAudioTaskPOService.updateById(po)
    }

    private fun checkTaskHasManualResult(id: Long): String {
        val task = bilibiliAudioTaskPOService.getById(id)!!
        if (task.status == TaskStatusEnum.SUCCESS.code) {
            // 先检查状态，已成功则返回
            return task.url
        }
        // 失败状态，但有结果，有名字，是手工填写的结果，也需要返回
        // if (task.url.isNotBlank() && task.name.isNotBlank()) {
        //     task.status = TaskStatusEnum.SUCCESS.code
        //     task.updateTime = null
        //     // TODO: 2022/3/26 这里的要抽出来
        //     bilibiliAudioTaskPOService.updateById(task)
        //     notifyService.notifyTaskResult(task)
        //     return task.url
        // }

        return ""
    }

    fun handleTask(task: BilibiliTask): String {
        try {

            if (!grayService.isGrayUser(task.openid)) {
                val successSubTask = findAliyunDriverSuccessSubTask(task)


                val needHandleSubTask = task.subTasks.filter { !successSubTask.contains(it) }

                val parentId = if (task.type == BilibiliTaskTypeEnum.MULTIPLE) {
                    task.subTasks
                    val userRootFolder = aliyundriveService.getFolderOrCreate(DEFAULT_ROOT_ID, task.openid)
                    aliyundriveService.getFolderOrCreate(userRootFolder, task.name!!)
                } else {
                    DEFAULT_ROOT_ID
                }

                needHandleSubTask.forEach { subTask ->
                    val targetFile = convertSubTask(subTask, 3)
                    val uploadDTO = aliyundriveService.uploadFile(targetFile, 5, parentId)

                    subTask.aliyundriverFileId = uploadDTO.file_id
                    updateSubTask(subTask)
                    FileUtils.deleteQuietly(targetFile)

                }

                val shareFileIds: List<String> = if (task.type == BilibiliTaskTypeEnum.MULTIPLE) {
                    listOf(parentId)
                } else {
                    task.subTasks.map { it.aliyundriverFileId!! }
                }


                val share = aliyundriveService.share(shareFileIds, 5)

                task.result = share.full_share_msg
                if (task.type == BilibiliTaskTypeEnum.FREE) {
                    task.name = share.share_name
                }

            } else {
                val audioFiles = task.subTasks.map { convertSubTask(it, 5) }

                val targetFile: File
                if (task.subTasks.size == 1) {
                    targetFile = audioFiles[0]
                } else {
                    targetFile = zipFiles(task, audioFiles)
                }

                val objectName = targetFile.name
                log.info { "开始上传文件 $objectName，大小：${FileUtils.sizeOf(targetFile) / (1024 * 1024)}M" }
                storageService.upload(objectName, targetFile.absolutePath)
                FileUtils.deleteQuietly(targetFile)
                val downloadUrl = storageService.getDownloadUrl(objectName)

                task.name = FilenameUtils.getBaseName(targetFile.name)
                task.result = downloadUrl

                audioFiles.forEach {
                    FileUtils.deleteQuietly(it)
                }
            }

            task.trimName()
            updateTaskStatus(task, TaskStatusEnum.SUCCESS)
            task.name = blackKeywordService.replaceBlackKeyword(task.name!!)
            notifyService.notifyTaskResult(task)

            return task.result
        } catch (e: Exception) {
            log.error(e.message, e)
            notifyService.notifyTaskFail(task)
            updateTaskStatus(task, TaskStatusEnum.FAIL)
        }
        return "处理失败"
    }

    private fun zipFiles(task: BilibiliTask, audioFiles: List<File>): File {
        val name = if (task.type == BilibiliTaskTypeEnum.MULTIPLE) {
            task.name!!
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


    fun convertSubTask(subTask: BilibiliSubTask, retry: Int): File {
        var count = 0
        var file: File? = null
        while (file?.exists() != true && count++ < retry) {
            try {
                file = convertSubTask(subTask)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
        return file!!
    }

    fun convertSubTask(subTask: BilibiliSubTask): File {

        val destinationFile = File("${workDir}/${subTask.trimTitle}.mp3")
        if (destinationFile.exists()) {
            log.info { "已存在 ${destinationFile.absolutePath} 直接使用" }
            return destinationFile
        }

        // 获取视频下载链接
        var downloadUrls = bilibiliService.getDashAudioPlayUrl(subTask.aid, subTask.cid)
        if (downloadUrls.isEmpty()) {
            downloadUrls = bilibiliService.getFlvPlayUrl(subTask.aid, subTask.cid)
        }

        // 下载视频到本地
        val source = "${workDir}/${subTask.cid}.m4s"
        val sourceFile = File(source)
        run downloadFile@ {
            downloadUrls.forEach {
                doDownloadBilibiliVideo(it, sourceFile, subTask.bvid, 3)
                if (sourceFile.exists()) {
                    return@downloadFile
                }
            }
        }


        // ffmpeg 文件如果是中文，可能会有些奇怪问题，使用 cid 作为唯一标识
        val target = "${workDir}/${subTask.cid}.mp3"
        FileUtils.deleteQuietly(File(target))
        ffmpeg(source, target, subTask.byteRate)


        // 最后恢复原本的文件名
        File(target).renameTo(destinationFile)
        FileUtils.deleteQuietly(File(target))
        FileUtils.deleteQuietly(sourceFile)
        return destinationFile
    }

    fun ffmpeg(inputPath: String, outputPath: String, byteRate: Long) {
        val ffmpeg = FFmpeg("ffmpeg")
        val ffprobe = FFprobe("ffprobe")

        val inputProbe = ffprobe.probe(inputPath)

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