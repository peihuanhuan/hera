package net.peihuan.hera.controller

import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.service.ChannelService
import net.peihuan.hera.service.UserService
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("/channel")
class ChannelController(private val channelService: ChannelService) {


    @PostMapping("create")
    fun listUsers(
        @RequestParam openid: String,
        @RequestParam source: Int
    ): JsonResult {
        return JsonResult.success(channelService.getChannelOrCreate())
    }

}