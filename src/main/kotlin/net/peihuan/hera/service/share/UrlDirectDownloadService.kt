package net.peihuan.hera.service.share

import mu.KotlinLogging
import net.peihuan.hera.constants.BilibiliTaskSourceTypeEnum
import net.peihuan.hera.domain.BilibiliSubTask
import net.peihuan.hera.domain.BilibiliTask
import net.peihuan.hera.service.remote.ShortUrlRemoteServiceWrapper
import net.peihuan.hera.service.storage.StorageService
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream


@Service
class UrlDirectDownloadService(
    private val storageService: StorageService,
    private val shortUrlRemoteServiceWrapper: ShortUrlRemoteServiceWrapper,

) : FileShareService {

    private val log = KotlinLogging.logger {}

    @Value("\${bilibili.workdir}")
    private val workDir: String? = null



    override fun needReConvert(subTask: BilibiliSubTask): Boolean {
        return true
    }


    override fun uploadAndAssembleTaskShare(task: BilibiliTask, convert: (subTask: BilibiliSubTask) -> Unit) {

        task.subTasks.forEach {
            if (needReConvert(it)) {
                convert(it)
            }
        }

        val targetFile: File
        if (task.subTasks.size == 1) {
            targetFile = task.subTasks[0].outFile!!
        } else {
            targetFile = zipFiles(task, task.subTasks.map { it.outFile!! })
        }

        val objectName = targetFile.name
        log.info { "开始上传文件 $objectName，大小：${FileUtils.sizeOf(targetFile) / (1024 * 1024)}M" }
        storageService.upload(objectName, targetFile.absolutePath)
        FileUtils.deleteQuietly(targetFile)
        var downloadUrl = storageService.getDownloadUrl(objectName)

        val shortUrl = shortUrlRemoteServiceWrapper.getShortUrl(downloadUrl)
        if (!shortUrl.isNullOrBlank()) {
            // 尝试替换为短链
            downloadUrl = shortUrl
        }

        task.name = FilenameUtils.getBaseName(targetFile.name)
        task.result = downloadUrl
    }


    private fun zipFiles(task: BilibiliTask, audioFiles: List<File>): File {
        val name = if (task.type == BilibiliTaskSourceTypeEnum.MULTIPLE) {
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


}
