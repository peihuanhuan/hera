package net.peihuan.hera.util

import java.util.*
import kotlin.math.sqrt

const val MIN_POINTS_CAN_EXCHANGE_MEMBER = 249



fun randomGaussianPoints(expect: Int, variance: Int): Int {
    // 方差
    // val variance = 12
    // 期望积分
    // todo  db 查，做一层15分钟的缓存
    // 控制一个月 10块钱
    // val expect = 25
    var point = 0
    while (point <= 0 || point >= 100) {
        point = (sqrt(variance.toDouble()) * Random().nextGaussian() + expect).toInt()
    }
    return point
}