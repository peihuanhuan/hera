package net.peihuan.hera.domain

import net.peihuan.hera.domain.annotation.NoArg
import net.peihuan.hera.persistent.po.PointsRecordPO
import net.peihuan.hera.persistent.po.SubscribePO

@NoArg
data class User(
    var id: Long,
    var appid: String,
    var openid: String,
    var unionid: String?,
    var nickname: String,
    var sex: Int,
    var province: String,
    var city: String,
    var country: String,
    var headimgurl: String,
    var privilege: String,
    var remark: String,
    var groupid: Int,

    var subscribes: List<SubscribePO>,
    var pointsRecords: List<PointsRecordPO>,
    var points: Int,
    var channels: List<Channel>,
) {
    // 当前关注状态
    fun getSubscribeStatus(): Int {
        if (subscribes.isNullOrEmpty()) {
            return -1
        }
        return subscribes.last().status ?: 0
    }
}
