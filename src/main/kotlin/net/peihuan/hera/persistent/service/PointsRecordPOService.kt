package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
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

    fun listPointsRecord(openid: String): List<PointsRecordPO> {
        return list(
            KtQueryWrapper(PointsRecordPO::class.java).eq(PointsRecordPO::openid, openid)
                .orderByDesc(PointsRecordPO::createTime)
        )
    }


}