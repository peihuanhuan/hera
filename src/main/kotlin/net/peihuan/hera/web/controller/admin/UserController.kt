package net.peihuan.hera.web.controller.admin

import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("admin/user")
class UserController(private val userService: UserService) {


    @GetMapping("list")
    fun listUsers(
        @RequestParam nickname: String?,
        @RequestParam current: Long?,
        @RequestParam size: Long?
    ): JsonResult {
        return JsonResult.success(userService.queryUsers(nickname, current ?: 0, size ?: 20))
    }

    @GetMapping("{openid}")
    fun listUsers(@PathVariable openid: String): JsonResult {
        return JsonResult.success(userService.getUserDetail(openid))
    }

}