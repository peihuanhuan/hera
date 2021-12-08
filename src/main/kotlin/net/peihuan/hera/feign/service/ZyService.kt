package net.peihuan.hera.feign.service

import net.peihuan.hera.feign.config.ZhongyongRequestInterceptor
import net.peihuan.hera.feign.dto.Page
import net.peihuan.hera.feign.dto.ZyOrder
import net.peihuan.hera.feign.dto.ZyResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "zy", url = "http://zy-api.wxthe.com", configuration = [ZhongyongRequestInterceptor::class])
interface ZyService {

    @GetMapping("/admin/data/order/life")
    fun queryH5Orders(@RequestParam("startTime") startTime: String,
                        @RequestParam("endTime") endTime: String,
                        @RequestParam("p") page: Int,
                        @RequestParam("perPage") perPage: Int): ZyResponse<Page<ZyOrder>>?


    @GetMapping("/addon/open/v1/life/promote/act")
    fun queryActs() : ZyResponse<List<Map<String, String>>>

    @GetMapping("/addon/open/v1/life/promote/link")
    fun queryActLink(
        @RequestParam("channel") channel: String,
        @RequestParam("act") act: String,
        @RequestParam("jump") jump: Int? = 0,
    ) : ZyResponse<Any>

    @GetMapping("/addon/open/v1/life/order/list")
    fun queryOrders(@RequestParam("channel") channel: String?,
                    @RequestParam("act") act: String?,
                    @RequestParam("startTime") startTime: String,
                    @RequestParam("endTime") endTime: String,
    ) : Any
}