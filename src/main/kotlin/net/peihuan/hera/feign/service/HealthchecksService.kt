package net.peihuan.hera.feign.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "healthchecks",
    url = "https://hc-ping.com/",
)
interface HealthchecksService {


    @GetMapping("{token}")
    fun ping(@PathVariable token: String)
}