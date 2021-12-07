package net.peihuan.hera.web.controller.admin

import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.service.ChannelService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("admin/channel")
class ChannelController(private val channelService: ChannelService) {


    @GetMapping("create")
    fun listUsers(
        @RequestParam openid: String,
        @RequestParam source: Int
    ): JsonResult {
        return JsonResult.success(channelService.getChannelOrCreate(openid, source))
    }

}