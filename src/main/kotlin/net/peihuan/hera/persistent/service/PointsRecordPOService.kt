package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.PointsRecordMapper
import net.peihuan.hera.persistent.po.PointsRecordPO
import org.springframework.stereotype.Service

@Service
class PointsRecordPOService : ServiceImpl<PointsRecordMapper, PointsRecordPO>() {

    fun addPointsRecord(openid: String, points: Int, desc: String) {
        val po = PointsRecordPO(openid = openid, points = points, desc = desc)
        save(po)
    }
}