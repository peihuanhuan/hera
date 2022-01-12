package net.peihuan.hera.service

import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import org.springframework.stereotype.Service
import kotlin.math.absoluteValue


@Service
class GrayService(private val cacheManage: CacheManage) {

    companion object val MAX_PERCENTAGE = 100

    fun isGrayUser(openid: String): Boolean {

        val openidHash = openid.hashCode().absoluteValue % MAX_PERCENTAGE

        val grayPercentage = cacheManage.getBizValue(BizConfigEnum.GRAY_USER_PERCENTAGE, "0").toInt()
        val grayUsers = cacheManage.getBizValue(BizConfigEnum.GRAY_USER).split("\n")
        return grayUsers.contains(openid) || openidHash < grayPercentage


    }

}