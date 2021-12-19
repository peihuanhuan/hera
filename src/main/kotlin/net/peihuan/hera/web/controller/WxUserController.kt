package net.peihuan.hera.web.controller

import me.chanjar.weixin.mp.api.WxMpService
import net.peihuan.hera.component.JwtTokenComponent
import net.peihuan.hera.constants.UserAuthorities
import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/wx/{appid}/user")
class WxUserController(
    val wxService: WxMpService,
    val userService: UserService,
    val userAuthorities: UserAuthorities,
    val jwtTokenComponent: JwtTokenComponent
) {


    @GetMapping("/authorizationUrl")
    fun buildAuthorizationUrl(
        @PathVariable appid: String,
        @RequestParam redirectUri: String, @RequestParam scope: String, @RequestParam state: String?
    ): JsonResult {
        require(wxService.switchover(appid)) { String.format("未找到对应appid=[%s]的配置，请核实！", appid) }
        return JsonResult.success(wxService.oAuth2Service.buildAuthorizationUrl(redirectUri, scope, state))
    }

    @GetMapping("jsapiSignature")
    fun get(@PathVariable appid: String, @RequestParam url: String) : JsonResult {
        require(wxService.switchover(appid)) { String.format("未找到对应appid=[%s]的配置，请核实！", appid) }
        return JsonResult.success(wxService.createJsapiSignature(url))
    }

    @PostMapping("login")
    fun getUserInfo(@PathVariable appid: String, @RequestParam code: String): JsonResult {
        require(wxService.switchover(appid)) { String.format("未找到对应appid=[%s]的配置，请核实！", appid) }
        val accessToken = wxService.oAuth2Service.getAccessToken(code)
        val userInfo = wxService.oAuth2Service.getUserInfo(accessToken, "zh_CN")
        val user = userService.getOrCreateAuthUser(userInfo)
        return JsonResult.success(jwtTokenComponent.generateUserToken(user.id, userAuthorities.NORMAL_USER))
    }

}