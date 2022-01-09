package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.RedPackageMapper
import net.peihuan.hera.persistent.po.RedPackagePO
import org.springframework.stereotype.Service

@Service
class RedPackagePOService : ServiceImpl<RedPackageMapper, RedPackagePO>() {

    fun findNotGiveupPackages(style: Int): List<RedPackagePO> {
        return list(
            KtQueryWrapper(RedPackagePO::class.java)
                .eq(RedPackagePO::giveUp, false)
                .eq(RedPackagePO::style, style)
                .orderByAsc(RedPackagePO::id)
        )
    }

    fun giveUp(id: Long, openid: String): Boolean {
        return getBaseMapper().tryGiveup(id, openid) > 0
    }


}