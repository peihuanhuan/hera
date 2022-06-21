package net.peihuan.hera.web.controller

import net.peihuan.baiduPanSDK.service.BaiduService
import net.peihuan.hera.domain.JsonResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/baidu/")
class BaiduUserController(
    val baiduService: BaiduService,
) {


    @GetMapping("/authorizationUrl")
    fun buildAuthorizationUrl(
        @RequestParam redirectUri: String, @RequestParam scope: String, @RequestParam state: String?
    ): JsonResult {
        return JsonResult.success(baiduService.getAuthorizeUrl(redirectUri))
    }

    @GetMapping("code")
    fun get(@RequestParam code: String, @RequestParam userId: String, @RequestParam redirectUri: String) : JsonResult {
        return JsonResult.success(baiduService.getAccessToken(userId, code, redirectUri))
    }


}