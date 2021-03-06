package net.peihuan.hera.web.controller

import net.peihuan.hera.constants.BilibiliTaskOutputTypeEnum
import net.peihuan.hera.constants.BilibiliTaskSourceTypeEnum
import net.peihuan.hera.constants.NotifyTypeEnum
import net.peihuan.hera.domain.JsonResult
import net.peihuan.hera.service.BVideo2AudioService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("bilibili")
class BilibiliController(private val bVideo2AudioService: BVideo2AudioService) {

    data class AudioRequest(
        val data: String,
        val type: Int,
        val outputType: Int = 0
    )


    @PostMapping("/audio")
    @PreAuthorize("hasAnyAuthority(@userAuthorities.NORMAL_USER)")
    fun convert2Audio(@RequestBody body: AudioRequest): JsonResult {
        val data = bVideo2AudioService.saveTask(
            body.data,
            BilibiliTaskSourceTypeEnum.getTypeEnum(body.type)!!,
            BilibiliTaskOutputTypeEnum.getTypeEnum(body.outputType)!!,
            NotifyTypeEnum.MESSAGE_TEMPLATE
        )
        return JsonResult.success(data)
    }
}