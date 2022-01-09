package net.peihuan.hera.web.controller

import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.service.OrderService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("order")
class OrderController(private val orderService: OrderService) {

    data class OrderRequest (
        val type: Int,
    )


    @PostMapping("")
    @PreAuthorize("hasAnyAuthority(@userAuthorities.NORMAL_USER)")
    fun convert2Audio(@RequestBody body:OrderRequest): JsonResult {
        return JsonResult.success(orderService.order(body.type))
    }
}