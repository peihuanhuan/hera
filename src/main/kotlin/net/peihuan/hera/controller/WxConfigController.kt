package net.peihuan.hera.controller

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.material.WxMpMaterial
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult
import net.peihuan.hera.domain.JsonResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

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


    @PostMapping("img/upload")
    fun uploadImg(
        @PathVariable appid: String,
        @RequestParam("name") name: String,
        @RequestParam("file") file: MultipartFile
    ): WxMpMaterialUploadResult {
        val createTempFile = File("/tmp/$name.jpg")
        createTempFile.createNewFile()
        file.transferTo(createTempFile)
        val wxMpMaterial = WxMpMaterial()
        wxMpMaterial.name = name
        wxMpMaterial.file = createTempFile
        val materialFileUpload = wxService.materialService.materialFileUpload(WxConsts.MaterialType.IMAGE, wxMpMaterial)
        createTempFile.delete()
        return materialFileUpload
    }


}