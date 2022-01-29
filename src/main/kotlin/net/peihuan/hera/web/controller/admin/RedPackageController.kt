package net.peihuan.hera.web.controller.admin

import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.service.RedPackageService
import org.apache.commons.io.FileUtils
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
@RequestMapping("package_red")
class RedPackageController(private val redPackageService: RedPackageService) {


    @PostMapping("admin/create")
    fun uploadRedPackage(@RequestParam("style") style: Int, @RequestParam("file") file: MultipartFile): JsonResult {
        val createTempFile = File.createTempFile("tmp", "txt")
        file.transferTo(createTempFile)
        val insert = redPackageService.saveBatchRedPackage(createTempFile, style)
        FileUtils.deleteQuietly(createTempFile)
        return JsonResult.success(insert)
    }

    @PostMapping("generate")
    @PreAuthorize("hasAnyAuthority(@userAuthorities.NORMAL_USER)")
    fun post(): JsonResult {
        redPackageService.generateHaibao()
        return JsonResult.success()
    }

}