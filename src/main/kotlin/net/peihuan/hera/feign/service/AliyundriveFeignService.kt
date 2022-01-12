package net.peihuan.hera.feign.service

import net.peihuan.hera.feign.config.AliyundriveInterceptor
import net.peihuan.hera.feign.dto.aliyundrive.*
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "aliyundrive",
    url = "https://api.aliyundrive.com",
    configuration = [AliyundriveInterceptor::class]
)
interface AliyundriveFeignService {


    @PostMapping("token/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): RefreshTokenDTO?

    @PostMapping("adrive/v3/file/list")
    fun listFile(@RequestBody request: ListFileRequest): Any

    @PostMapping("v2/file/get")
    fun get(@RequestBody request: GetFileRequest): GetFileDTO

    @PostMapping("adrive/v2/file/createWithFolders")
    fun createWithFolders(@RequestBody request: CreateWithFoldersRequest) : CreateWithFoldersDTO

    @PostMapping("v2/file/complete")
    fun completeUpload(@RequestBody request: CompleteUploadRequest) : CompleteUploadDTO

    @PostMapping("adrive/v2/share_link/create")
    fun share(@RequestBody request: ShareRequest) : ShareDTO
}