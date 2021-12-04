package net.peihuan.hera.domain

import net.peihuan.hera.domain.annotation.NoArg
import net.peihuan.hera.persistent.po.PointsRecordPO
import net.peihuan.hera.persistent.po.SubscribePO

@NoArg
data class User(
    var id: Long,
    var appid: String,
    val openid: String,
    val unionid: String,
    val nickname: String,
    val sex: Int,
    val province: String,
    val city: String,
    val country: String,
    val headimgurl: String,
    val privilege: String,
    val remark: String,
    val groupid: Int,

    var subscribes: List<SubscribePO>,
    var pointsRecords: List<PointsRecordPO>,
    var points: Int,
) {
    // 当前关注状态
    fun getSubscribeStatus(): Int {
        if (subscribes.isNullOrEmpty()) {
            return -1
        }
        return subscribes.last().status ?: 0
    }
}
