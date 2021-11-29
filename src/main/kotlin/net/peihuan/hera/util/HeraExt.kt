package net.peihuan.hera.util

import net.peihuan.hera.constants.INVITER
import net.peihuan.hera.domain.InviterUser
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import kotlin.math.sqrt

const val MIN_POINTS_CAN_EXCHANGE_MEMBER = 249


fun randomGaussianPoints(expect: Int, variance: Int): Int {
    // 方差
    // val variance = 12
    // 期望积分
    // 控制一个月 10块钱
    // val expect = 25
    var point = 0
    while (point <= 0 || point >= 100) {
        point = (sqrt(variance.toDouble()) * Random().nextGaussian() + expect).toInt()
    }
    return point
}


fun encodeInviter(activityId: Long, openid: String): String {
    val au = InviterUser(activityId, openid)
    return "$INVITER${URLEncoder.encode(au.toJson(), "utf-8")}"
}

fun decodeInviter(encodeStr: String): InviterUser? {
    if (!encodeStr.startsWith(INVITER)) {
        return null
    }
    val decode = URLDecoder.decode(encodeStr.removePrefix(INVITER), "utf-8")
    return decode.toBean<InviterUser>()
}