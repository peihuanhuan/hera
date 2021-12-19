package net.peihuan.hera.service

import mu.KotlinLogging
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.feign.dto.aliyundrive.*
import net.peihuan.hera.feign.service.AliyundriveFeignService
import net.peihuan.hera.util.JsUtil
import net.peihuan.hera.util.toJson
import net.peihuan.hera.util.upload
import org.apache.commons.codec.digest.DigestUtils
import org.joda.time.DateTime
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.annotation.PostConstruct


@Service
class AliyundriveService(private val aliyundriveFeignService: AliyundriveFeignService) {

    private val log = KotlinLogging.logger {}

    var accassToken =
        "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJiOTIwNzA1Y2U3YjY0ZDdmOTYyOTEwN2ZhZjQ4NmNiOSIsImN1c3RvbUpzb24iOiJ7XCJjbGllbnRJZFwiOlwiMjVkelgzdmJZcWt0Vnh5WFwiLFwiZG9tYWluSWRcIjpcImJqMjlcIixcInNjb3BlXCI6W1wiRFJJVkUuQUxMXCIsXCJTSEFSRS5BTExcIixcIkZJTEUuQUxMXCIsXCJVU0VSLkFMTFwiLFwiU1RPUkFHRS5BTExcIixcIlNUT1JBR0VGSUxFLkxJU1RcIixcIkJBVENIXCIsXCJPQVVUSC5BTExcIixcIklNQUdFLkFMTFwiLFwiSU5WSVRFLkFMTFwiLFwiQUNDT1VOVC5BTExcIl0sXCJyb2xlXCI6XCJ1c2VyXCIsXCJyZWZcIjpcImh0dHBzOi8vd3d3LmFsaXl1bmRyaXZlLmNvbS9cIixcImRldmljZV9pZFwiOlwiMGI5YTE1ZmMzOTBjNDA1MWFlZTkxZGIwZTU2YjlmYTlcIn0iLCJleHAiOjE2MzkzMjY4MDEsImlhdCI6MTYzOTMxOTU0MX0.cCLUDMzGXmNryVKTFBdNK5x9viBFon1q4bJRPRhB9zc2QeR0CFx3xaJiDyNyL5qmwTuBOwjlJn0wZKRPuo0qWfLYA0ehUOw5390VTnZqsPOypasf8NZqaz7I9epMAxeOOhSM9KxeMOLFigiIDN7XUwUPP6a-F3cKviP9jgfPCiQ"
    var refreshToken = "4e328ddd40f141469cd686db5f347ba8"
    var driveId = "1115450"
    var tokeType = "Bearer"

    val parentId = "61b5b695fa43037eca8d44cb85bb457b299b5005"

    companion object {
        var ALIYUN_DRIVER_TOKEN = ""
    }

    private val part_max_size: Long = 10 * 1024 * 1024L

    @PostConstruct
    fun setToken() {
        ALIYUN_DRIVER_TOKEN = "$tokeType $accassToken"
    }

    fun share(fileId: String) : ShareDTO{
        return share(listOf(fileId))
    }

    fun share(fileIds:List<String>) : ShareDTO{
        if(fileIds.size > 100) {
            throw BizException.buildBizException("最大分享不能超过100个")
        }
        val shareRequest =
            ShareRequest(drive_id = driveId, expiration = DateTime.now().plusDays(7).toString(), file_id_list = fileIds)
        return aliyundriveFeignService.share(shareRequest)
    }

    fun uploadFile(file: File) :CreateWithFoldersDTO {

        val part: Int = if (file.length() / part_max_size == 0L) {
            (file.length() / part_max_size).toInt()
        } else {
            (file.length() / part_max_size).toInt() + 1
        }
        val mutableListOf = mutableListOf<PartInfo>()
        for (i in 0..part) {
            mutableListOf.add(PartInfo(part_number = i + 1))
        }

        val execJs = JsUtil.execJs(accassToken).substring(0, 16)
        val left = if (file.length() == 0L) {
            0L
        } else {
            (execJs.toBigInteger(16).mod(file.length().toBigInteger())).toLong()
        }
        val right = minOf(left + 8, file.length())

        val pRoofCode = Base64.getEncoder().encodeToString(file.readBytes().copyOfRange(left.toInt(), right.toInt()))
        val createWithFoldersRequest = CreateWithFoldersRequest(
            drive_id = driveId,
            parent_file_id = parentId,
            part_info_list = mutableListOf,
            name = file.name,
            type = "file",
            size = file.length().toInt(),
            proof_version = "v1",
            content_hash_name = "sha1",
            check_name_mode = "auto_rename",
            content_hash = DigestUtils.sha1Hex(file.inputStream()),
            // mime_extension = "txt", // 应该无用
            // mime_type = "text/plain", // 应该无用
            proof_code = pRoofCode
        )
        log.info { "createWithFolders 参数: ${createWithFoldersRequest.toJson()}" }
        val createWithFoldersDTO = aliyundriveFeignService.createWithFolders(createWithFoldersRequest)

        if (createWithFoldersDTO.part_info_list == null) {
            return createWithFoldersDTO
        }

        // 分段上传
        val fileInputStream = FileInputStream(file)
        val buf = ByteArray(part_max_size.toInt())
        var partNo = 0
        var length: Int
        while (fileInputStream.read(buf).also { length = it } != -1) {
            upload(createWithFoldersDTO.part_info_list[partNo].upload_url, buf.copyOfRange(0, length))
            partNo++
        }
        fileInputStream.close()


        // 完成上传
        val completeUploadRequest = CompleteUploadRequest(drive_id = driveId, file_id = createWithFoldersDTO.file_id, upload_id = createWithFoldersDTO.upload_id)
        aliyundriveFeignService.completeUpload(completeUploadRequest)


        return createWithFoldersDTO
    }

    fun listFile(): Any {
        return aliyundriveFeignService.listFile(ListFileRequest(limit = 100, drive_id = driveId, parent_file_id = parentId))
    }

    fun refreshToken() {
        val refreshTokenDTO = aliyundriveFeignService.refreshToken(RefreshTokenRequest(refreshToken))
        if (refreshTokenDTO == null) {
            log.error { "刷新access_token失败" }
            return
        }
        accassToken = refreshTokenDTO.access_token
        refreshToken = refreshTokenDTO.refresh_token
        driveId = refreshTokenDTO.default_drive_id
        tokeType = refreshTokenDTO.token_type
        log.info { "刷新access_token完成 $accassToken" }
    }

}
