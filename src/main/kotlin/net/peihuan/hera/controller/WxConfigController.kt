package net.peihuan.hera.controller

import me.chanjar.weixin.mp.api.WxMpService
import net.peihuan.hera.domain.JsonResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/wx/{appid}")
class WxConfigController(val wxService: WxMpService) {


    @PostMapping("/create/qrcode")
    fun createQrcode(@PathVariable appid: String, @RequestParam sceneStr: String): JsonResult {
        val qrCodeTicket = wxService.qrcodeService.qrCodeCreateLastTicket(sceneStr)
        return JsonResult.success()
                .add("appid", appid)
                .add("sceneStr", sceneStr)
                .add("qrCodeTicket", qrCodeTicket)
    }


    @GetMapping("tags")
    fun getTags(@PathVariable appid: String): JsonResult {
        return JsonResult.success(wxService.userTagService.tagGet())
    }

}