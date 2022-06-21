package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.RedPackageCoverMapper
import net.peihuan.hera.persistent.po.RedPackageCoverPO
import org.springframework.stereotype.Service

@Service
class RedPackageCoverPOService : ServiceImpl<RedPackageCoverMapper, RedPackageCoverPO>() {

    fun findNotGiveupPackages(style: Int): List<RedPackageCoverPO> {
        return list(
            KtQueryWrapper(RedPackageCoverPO::class.java)
                .eq(RedPackageCoverPO::giveUp, false)
                .eq(RedPackageCoverPO::style, style)
                .orderByAsc(RedPackageCoverPO::id)
        )
    }

    fun giveUp(id: Long, openid: String): Boolean {
        return getBaseMapper().tryGiveup(id, openid) > 0
    }


}