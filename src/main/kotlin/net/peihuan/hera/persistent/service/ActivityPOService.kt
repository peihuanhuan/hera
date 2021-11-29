package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.ActivityMapper
import net.peihuan.hera.persistent.po.ActivityPO
import org.springframework.stereotype.Service

@Service
class ActivityPOService : ServiceImpl<ActivityMapper, ActivityPO>() {

    fun getByKeyword(keyword: String): ActivityPO? {
        return getOne(
            KtQueryWrapper(ActivityPO::class.java)
                .eq(ActivityPO::keyword, keyword)
                .orderByDesc(ActivityPO::createTime)
                .last("limit 1")
        )
    }

}