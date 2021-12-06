package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.constants.OrderSourceEnum
import net.peihuan.hera.persistent.mapper.ChannelMapper
import net.peihuan.hera.persistent.po.ChannelPO
import org.springframework.stereotype.Service

@Service
class ChannelPOService : ServiceImpl<ChannelMapper, ChannelPO>() {


    fun getChannelPOs(openid: String): List<ChannelPO> {
        return list(KtQueryWrapper(ChannelPO::class.java).eq(ChannelPO::openid, openid))
    }

    fun getChannelPO(openid: String, source: OrderSourceEnum): ChannelPO? {
        return getOne(
            KtQueryWrapper(ChannelPO::class.java)
                .eq(ChannelPO::openid, openid)
                .eq(ChannelPO::source, source)
        )
    }

}