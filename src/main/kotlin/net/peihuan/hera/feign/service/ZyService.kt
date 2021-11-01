package net.peihuan.hera.feign.service

import net.peihuan.hera.feign.config.ZhongyongRequestInterceptor
import net.peihuan.hera.feign.dto.Page
import net.peihuan.hera.feign.dto.ZyOrder
import net.peihuan.hera.feign.dto.ZyResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "zy", url = "http://zy-api.wxthe.com/admin/data/", configuration = [ZhongyongRequestInterceptor::class])
interface ZyService {

    @GetMapping("order/life")
    fun queryH5Orders(@RequestParam("startTime") startTime: String,
                        @RequestParam("endTime") endTime: String,
                        @RequestParam("p") page: Int,
                        @RequestParam("perPage") perPage: Int): ZyResponse<Page<ZyOrder>>?
}