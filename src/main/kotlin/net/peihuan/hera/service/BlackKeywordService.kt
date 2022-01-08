package net.peihuan.hera.service

import mu.KotlinLogging
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.persistent.service.SignPOService
import org.springframework.stereotype.Service

@Service
class BlackKeywordService(private val signPOService: SignPOService,
                          private val cacheManage: CacheManage,
                          private val userPointsService: UserPointsService) {

    private val logger = KotlinLogging.logger {}


    fun replaceBlackKeyword(data: String): String {
        var newData: String = data
        val bizValue = cacheManage.getBizValue(BizConfigEnum.BLACK_KEY_WORD, "")
        val blacks = bizValue.split("\n")
        blacks.forEach {
            if (it.isNotBlank()) {
                newData = newData.replace(it, "*")
            }
        }
        return newData
    }

}