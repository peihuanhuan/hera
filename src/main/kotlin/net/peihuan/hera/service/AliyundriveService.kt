package net.peihuan.hera.service

import mu.KotlinLogging
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.feign.dto.aliyundrive.*
import net.peihuan.hera.feign.service.AliyundriveFeignService
import net.peihuan.hera.util.JsUtil
import net.peihuan.hera.util.toJson
import net.peihuan.hera.util.upload
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.joda.time.DateTime
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.annotation.PostConstruct


@Service
class AliyundriveService(
    private val aliyundriveFeignService: AliyundriveFeignService,
    private val notifyService: NotifyService,
    private val blackKeywordService: BlackKeywordService,
    private val cacheManage: CacheManage,
) {

    private val log = KotlinLogging.logger {}

    var accassToken = ""
    var refreshToken = ""
    var driveId = ""
    var tokeType = "Bearer"

    // 根目录


    companion object {
        var ALIYUN_DRIVER_TOKEN = ""
        var DEFAULT_ROOT_ID = ""
    }

    private val part_max_size: Long = 10 * 1024 * 1024L

    @PostConstruct
    fun setToken() {
        refreshToken = cacheManage.getBizValue(BizConfigEnum.ALI_YUN_DRIVER_REFRESH_TOKEN)
        DEFAULT_ROOT_ID = cacheManage.getBizValue(BizConfigEnum.ALI_YUN_DRIVER_DEFAULT_ROOT)
    }

    fun get(fileId: String): GetFileDTO? {
        return try {
            aliyundriveFeignService.get(GetFileRequest(drive_id = driveId, file_id = fileId))
        } catch (e: Exception) {
            log.error(e.message)
            null
        }
    }

    fun checkFileExisted(fileId: String): Boolean {
        val fileDTO = get(fileId)
        if (fileDTO != null && !fileDTO.trashed) {
            //检查是否被删除等情况
            return true
        }
        return false
    }


    fun share(fileIds: List<String>, retry: Int): ShareDTO {
        if (fileIds.size > 100) {
            throw BizException.buildBizException("最大分享不能超过100个")
        }
        val shareRequest =
            ShareRequest(drive_id = driveId, expiration = DateTime.now().plusDays(7).toString(), file_id_list = fileIds)

        var retryTime = 0
        while (retryTime++ < retry) {
            try {
                return aliyundriveFeignService.share(shareRequest)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
        throw BizException.buildBizException("阿里云盘分享失败")
    }

    fun uploadFile(file: File , retry: Int, parentId: String = DEFAULT_ROOT_ID): CreateWithFoldersDTO {
        var time = 0
        while (time++ < retry) {
            try {
                return uploadFile(file, parentId)
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
        throw BizException.buildBizException("阿里云盘上传失败")
    }
    fun uploadFile(file: File, parentId: String = DEFAULT_ROOT_ID): CreateWithFoldersDTO {

        var uploadFile = file
        if (file.absolutePath.endsWith(".mp3")) {
            uploadFile = buildFakeFile(file)
        }

        val part: Int = if (uploadFile.length() / part_max_size == 0L) {
            (uploadFile.length() / part_max_size).toInt()
        } else {
            (uploadFile.length() / part_max_size).toInt() + 1
        }
        val mutableListOf = mutableListOf<PartInfo>()
        for (i in 0..part) {
            mutableListOf.add(PartInfo(part_number = i + 1))
        }

        val execJs = JsUtil.execJs(accassToken).substring(0, 16)
        val left = if (uploadFile.length() == 0L) {
            0L
        } else {
            (execJs.toBigInteger(16).mod(uploadFile.length().toBigInteger())).toLong()
        }
        val right = minOf(left + 8, uploadFile.length())

        val pRoofCode =
            Base64.getEncoder().encodeToString(uploadFile.readBytes().copyOfRange(left.toInt(), right.toInt()))
        val createWithFoldersRequest = CreateWithFoldersRequest(
            drive_id = driveId,
            parent_file_id = parentId,
            part_info_list = mutableListOf,
            name = blackKeywordService.replaceBlackKeyword(file.name),
            type = "file",
            size = uploadFile.length().toInt(),
            proof_version = "v1",
            content_hash_name = "sha1",
            check_name_mode = "auto_rename",
            content_hash = DigestUtils.sha1Hex(uploadFile.inputStream()),
            proof_code = pRoofCode
        )
        log.info { "createWithFolders 参数: ${createWithFoldersRequest.toJson()}" }
        val createWithFoldersDTO = aliyundriveFeignService.createWithFolders(createWithFoldersRequest)

        if (createWithFoldersDTO.part_info_list == null) {
            return createWithFoldersDTO
        }

        // 分段上传
        val fileInputStream = FileInputStream(uploadFile)
        val buf = ByteArray(part_max_size.toInt())
        var partNo = 0
        var length: Int
        while (fileInputStream.read(buf).also { length = it } != -1) {
            log.info("分段上传 {} partNo: {}", file.name, partNo)
            upload(createWithFoldersDTO.part_info_list[partNo].upload_url, buf.copyOfRange(0, length))
            partNo++
        }
        fileInputStream.close()


        // 完成上传
        val completeUploadRequest = CompleteUploadRequest(
            drive_id = driveId,
            file_id = createWithFoldersDTO.file_id,
            upload_id = createWithFoldersDTO.upload_id!!
        )
        aliyundriveFeignService.completeUpload(completeUploadRequest)

        FileUtils.deleteQuietly(uploadFile)

        return createWithFoldersDTO
    }

    fun getFolderOrCreate(parentId: String, name: String): String {
        val createWithFoldersRequest = CreateWithFoldersRequest(
            drive_id = driveId,
            check_name_mode = "refuse",
            parent_file_id = parentId,
            name = name,
            type = "folder",
        )
        log.info { "createWithFolders 参数: ${createWithFoldersRequest.toJson()}" }
        val createWithFoldersDTO = aliyundriveFeignService.createWithFolders(createWithFoldersRequest)
        return createWithFoldersDTO.file_id
    }

    fun createFolder(parentId: String, name: String): String {
        val createWithFoldersRequest = CreateWithFoldersRequest(
            drive_id = driveId,
            check_name_mode = "auto_rename",
            parent_file_id = parentId,
            name = name,
            type = "folder",
        )
        log.info { "createWithFolders 参数: ${createWithFoldersRequest.toJson()}" }
        val createWithFoldersDTO = aliyundriveFeignService.createWithFolders(createWithFoldersRequest)
        return createWithFoldersDTO.file_id
    }

    // https://www.garykessler.net/library/file_sigs.html
    // https://en.wikipedia.org/wiki/List_of_file_signatures
    fun buildFakeFile(file: File): File {
        val fakeFile =
            File(FilenameUtils.getFullPath(file.absolutePath) + FilenameUtils.getBaseName(file.absolutePath) + "-fake.mp3")

        if (fakeFile.exists()) {
            fakeFile.delete()
        }
        // 添加图片的魔数
        // fakeFile.writeBytes(
        //     byteArrayOf(
        //         "ff".toInt(16).toByte(),
        //         ("d8".toInt(16).toByte()),
        //         ("ff".toInt(16).toByte())
        //     )
        // )
        fakeFile.writeBytes(
            byteArrayOf(
                "46".toInt(16).toByte(),
                "4C".toInt(16).toByte(),
                "56".toInt(16).toByte(),
                "01".toInt(16).toByte(),
                // "69".toInt(16).toByte(),
                // "73".toInt(16).toByte(),
                // "6F".toInt(16).toByte(),
                // "6D".toInt(16).toByte(),
            )
        )
        fakeFile.appendBytes(file.readBytes())
        return fakeFile
    }

    fun listFile(): Any {
        return aliyundriveFeignService.listFile(
            ListFileRequest(
                limit = 100,
                drive_id = driveId,
                parent_file_id = DEFAULT_ROOT_ID
            )
        )
    }

    @Scheduled(fixedDelay = 1800_000)
    fun refreshToken() {
        val refreshTokenDTO = aliyundriveFeignService.refreshToken(RefreshTokenRequest(refreshToken))
        if (refreshTokenDTO == null) {
            log.error { "刷新access_token失败" }
            notifyService.notifyAdmin("刷新access_token失败")
            return
        }
        accassToken = refreshTokenDTO.access_token
        refreshToken = refreshTokenDTO.refresh_token
        driveId = refreshTokenDTO.default_drive_id
        tokeType = refreshTokenDTO.token_type
        ALIYUN_DRIVER_TOKEN = "$tokeType $accassToken"
        // 更新token
        cacheManage.updateBizValue(BizConfigEnum.ALI_YUN_DRIVER_REFRESH_TOKEN, refreshToken)
        log.info { "刷新access_token完成 $accassToken" }
    }

}
