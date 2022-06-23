package net.peihuan.hera.service

import mu.KotlinLogging
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFmpegUtils
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.peihuan.hera.constants.*
import net.peihuan.hera.domain.BilibiliSubTask
import net.peihuan.hera.domain.BilibiliTask
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.feign.dto.bilibili.Quality
import net.peihuan.hera.persistent.po.BilibiliTaskPO
import net.peihuan.hera.persistent.service.BilibiliAudioPOService
import net.peihuan.hera.persistent.service.BilibiliAudioTaskPOService
import net.peihuan.hera.persistent.service.PersistentLogService
import net.peihuan.hera.service.convert.BilibiliTaskConvertService
import net.peihuan.hera.service.share.AliyundriveService
import net.peihuan.hera.service.share.BaiduPanService
import net.peihuan.hera.service.share.FileShareService
import net.peihuan.hera.service.share.UrlDirectDownloadService
import net.peihuan.hera.util.blockWithTry
import net.peihuan.hera.util.currentUserOpenid
import net.peihuan.hera.util.doDownloadBilibiliVideo
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Service
class BVideo2AudioService(
    private val bilibiliService: BilibiliService,
    private val notifyService: NotifyService,
    private val cacheManage: CacheManage,
    private val userService: UserService,
    private val persistentLogService: PersistentLogService,
    private val aliyundriveService: AliyundriveService,
    private val urlDirectDownloadService: UrlDirectDownloadService,
    private val configService: ConfigService,
    private val bilibiliAudioTaskPOService: BilibiliAudioTaskPOService,
    private val bilibiliAudioPOService: BilibiliAudioPOService,
    private val bilibiliTaskConvertService: BilibiliTaskConvertService,
    private val baiduPanService: BaiduPanService,
    private val grayService: GrayService,
    private val blackKeywordService: BlackKeywordService
) {

    private val log = KotlinLogging.logger {}
    private val executorService = Executors.newFixedThreadPool(5)

    private val processingTasks = mutableSetOf<Long>()

    @Value("\${bilibili.workdir}")
    private val workDir: String? = null


    @Transactional
    fun saveTask(
        data: String,
        type: BilibiliTaskSourceTypeEnum,
        outputTypeEnum: BilibiliTaskOutputTypeEnum,
        notifyType: NotifyTypeEnum
    ): Int {

        if (outputTypeEnum == BilibiliTaskOutputTypeEnum.VIDEO) {

            if (!grayService.isGrayVideoUser(currentUserOpenid)) {
                throw BizException.buildBizException("暂不是内测者，进群临时开通权限")
            }
        }

        val notHandleTask = bilibiliAudioTaskPOService.findByOpenidAndStatus(currentUserOpenid, TaskStatusEnum.DEFAULT)
        if (notHandleTask.isNotEmpty()) {
            throw BizException.buildBizException("请等待上一个任务执行完成~")
        }

        val task: BilibiliTask = generateBilibiliTask(data, type, outputTypeEnum, notifyType)

        val freeLimit = cacheManage.getBizValue(BizConfigEnum.MAX_FREE_LIMIT, "10").toInt()
        val videoLimit = cacheManage.getBizValue(BizConfigEnum.VIDEO_LIMIT, "45").toInt()
        val multiPLimit = cacheManage.getBizValue(BizConfigEnum.MAX_P_LIMIT, "35").toInt()
        val maxDurationMinute = cacheManage.getBizValue(BizConfigEnum.MAX_DURATION_MINUTE, "300").toInt()

        try {
            task.validTask(
                freeLimit = freeLimit,
                videoLimit = videoLimit,
                multiPLimit = multiPLimit,
                allowMaxDurationMinutes = maxDurationMinute
            )
        } catch (e: Exception) {
            persistentLogService.saveLog(e.message ?: "")
            throw e
        }


        val taskPO: BilibiliTaskPO = bilibiliTaskConvertService.convert2BilibiliTaskPO(task)
        bilibiliAudioTaskPOService.save(taskPO)
        task.setId(taskPO.id!!)
        val subTaskPOs = task.subTasks.map { bilibiliTaskConvertService.convert2BilibiliSubTaskPO(it) }
        bilibiliAudioPOService.saveBatch(subTaskPOs)

        return task.subTaskSize
    }

    fun generateBilibiliTask(
        requestData: String,
        type: BilibiliTaskSourceTypeEnum,
        outputTypeEnum: BilibiliTaskOutputTypeEnum,
        notifyType: NotifyTypeEnum
    ): BilibiliTask {
        var bilibiliVideos = bilibiliService.resolve2BilibiliVideos(requestData)
        if (bilibiliVideos.isEmpty()) {
            throw BizException.buildBizException("没解析到B站视频~")
        }

        var taskName = ""
        if (type == BilibiliTaskSourceTypeEnum.MULTIPLE) {
            val firstVideo = bilibiliVideos.first()
            bilibiliVideos = bilibiliService.findAllBilibiliVideos(firstVideo)
            taskName = bilibiliService.getViewByBvid(firstVideo.bvid).title
        }

        val task = BilibiliTask(
            request = requestData,
            name = taskName,
            type = type,
            notifyType = notifyType,
            openid = currentUserOpenid,
            outputType = outputTypeEnum
        )
        val subTasks =
            bilibiliVideos.map { BilibiliSubTask(parentTask = task, bilibiliVideo = it, openid = currentUserOpenid) }
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
        val subTasks = subTaskPOs.map { bilibiliTaskConvertService.convert2BilibiliSubTask(task, it) }
        task.addSubTasks(subTasks)

        return handleTask(task)
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
        return ""
    }

    fun determineUserFileStorageOrder(task: BilibiliTask): List<FileShareService> {
        val services: MutableList<FileShareService>

        if (!grayService.isDirDownloadUser(task.openid)) {
            // TODO: 2022/6/14
        }


        val userFileStorageConfig = configService.getUserFileStorageConfig(task.openid)
        val aliFirst = mutableListOf(aliyundriveService, baiduPanService, urlDirectDownloadService)
        val baiduFirst = mutableListOf(baiduPanService, aliyundriveService, urlDirectDownloadService)



        services = if (userFileStorageConfig == null) {
            // val user = userService.getSimpleUser(task.openid) ?: return baiduFirst
            // if (user.createTime.after(DateTime(2022,6,16,21,0).toDate())) {
                // 新用户，优先用百度云盘
                // baiduFirst
            // } else {
                baiduFirst
            // }
        } else if (userFileStorageConfig.value!! == FILE_STORAGE_PLATFORM_ALI) {
            aliFirst
        } else if (userFileStorageConfig.value!! == FILE_STORAGE_PLATFORM_BAIDU){
            baiduFirst
        } else {
            aliFirst
        }

        if (!checkCanUseAliyunPan(task)) {
            services.remove(aliyundriveService)
        }

        return services
    }


    fun handleTask(task: BilibiliTask): String {
        try {
            val fileShareServices: List<FileShareService> = determineUserFileStorageOrder(task)

            run breaking@{
                fileShareServices.forEach { service ->
                    try {
                        service.uploadAndAssembleTaskShare(task) {
                            convertSubTask(task, it, 3)
                        }
                        return@breaking
                    } catch (e: Exception) {
                        log.error(e.message, e)
                    }
                }
            }
            if(task.result.isBlank()) {
                throw BizException.buildBizException("全都处理失败了")
            }

            task.cleanFiles()

            task.shortName()
            updateTaskStatus(task, TaskStatusEnum.SUCCESS)
            task.name = blackKeywordService.replaceBlackKeyword(task.name!!)

            blockWithTry(retryTime = 15) { notifyService.notifyTaskResult(task) }

            return task.result
        } catch (e: Exception) {
            log.error(e.message, e)
            notifyService.notifyTaskFail(task)
            updateTaskStatus(task, TaskStatusEnum.FAIL)
        }
        return "处理失败"
    }

    fun checkCanUseAliyunPan(task: BilibiliTask): Boolean {
        val aliyunBlackFileName = cacheManage.getBizValueList(BizConfigEnum.ALI_DRIVER_BLACK_FILE_NAME)
        aliyunBlackFileName.forEach { blackWord ->
            task.subTasks.forEach { subTask ->
                if (subTask.originalTitle.lowercase().contains(blackWord)) {
                    return false
                }
            }
        }
        return true
    }


    fun convertSubTask(task: BilibiliTask, subTask: BilibiliSubTask, retry: Int): File {
        var count = 0
        var file: File? = null
        while (file?.exists() != true && count++ < retry) {
            try {
                file = convertSubTask(task, subTask)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
        return file!!
    }

    fun convertSubTask(task: BilibiliTask, subTask: BilibiliSubTask): File {
        if (subTask.outFile != null && subTask.outFile!!.exists()) {
            return subTask.outFile!!
        }
        val extension = if (subTask.outputType == BilibiliTaskOutputTypeEnum.VIDEO) "mp4" else "mp3"

        val destinationFile = File("${workDir}/${subTask.trimTitle}.$extension")
        if (destinationFile.exists()) {
            log.info { "已存在 ${destinationFile.absolutePath} 直接使用" }
            subTask.outFile = destinationFile
            return destinationFile
        }

        // 获取视频下载链接
        var downloadUrls: List<String>
        if ((subTask.sid ?: "").isNotBlank()) {
            downloadUrls = bilibiliService.getMusicUrl(subTask.sid!!)
        } else if (subTask.outputType == BilibiliTaskOutputTypeEnum.VIDEO) {
            downloadUrls = bilibiliService.getFlvPlayUrl(subTask.aid, subTask.cid, Quality.P_1080)
        } else {
            downloadUrls = bilibiliService.getDashAudioPlayUrl(subTask.aid, subTask.cid)
            if (downloadUrls.isEmpty()) {
                downloadUrls = bilibiliService.getFlvPlayUrl(subTask.aid, subTask.cid, Quality.P_360)
            }
        }

        // 下载视频到本地
        val source = "${workDir}/${subTask.cid}.m4s"
        val sourceFile = File(source)
        run downloadFile@{
            downloadUrls.forEach {
                doDownloadBilibiliVideo(it, sourceFile, subTask.bvid, 3)
                if (sourceFile.exists()) {
                    return@downloadFile
                }
            }
        }


        // ffmpeg 文件如果是中文，可能会有些奇怪问题，使用 cid 作为唯一标识
        val ffmpegTargetFile = "${workDir}/${subTask.cid}.$extension"
        ffmpeg(source, ffmpegTargetFile, subTask.byteRate)


        // 最后恢复原本的文件名
        File(ffmpegTargetFile).renameTo(destinationFile)
        FileUtils.deleteQuietly(File(ffmpegTargetFile))
        FileUtils.deleteQuietly(sourceFile)
        subTask.outFile = destinationFile
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