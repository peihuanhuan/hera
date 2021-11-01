package net.peihuan.hera.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("")
class CommonController() {

    @GetMapping("/heartbeat")
    fun menuCreateSample(): String {
        return "i'm fine!"
    }
}