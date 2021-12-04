package net.peihuan.hera.service

import mu.KotlinLogging
import net.peihuan.hera.persistent.po.PointsRecordPO
import net.peihuan.hera.persistent.po.UserPointsPO
import net.peihuan.hera.persistent.service.PointsRecordPOService
import net.peihuan.hera.persistent.service.UserPointsPOService
import net.peihuan.hera.util.toJson
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserPointsService(private val userPointsPOService: UserPointsPOService,
                        private val pointsRecordPOService: PointsRecordPOService) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun addUserPoints(openid: String, points: Int, desc: String): UserPointsPO {
        var userPointsPO = userPointsPOService.getByOpenid(openid)
        if (userPointsPO == null) {
            userPointsPO = UserPointsPO(openid = openid)
        }
        val newPoints = (userPointsPO.points ?: 0) + points
        userPointsPO.points = newPoints

        logger.info { "更新用户积分  ${userPointsPO.toJson()}" }
        userPointsPOService.saveOrUpdate(userPointsPO)

        pointsRecordPOService.addPointsRecord(openid, points, desc)

        return userPointsPO
    }


    fun getUserPoints(openid: String): Int {
        val userPointsPO = userPointsPOService.getByOpenid(openid) ?: return 0
        return userPointsPO.points ?: 0
    }

    fun getPointRecords(openid: String): List<PointsRecordPO> {
        return pointsRecordPOService.listPointsRecord(openid)
    }


}