package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.WxOrderMapper
import net.peihuan.hera.persistent.po.WxOrderPO
import org.springframework.stereotype.Service

@Service
class WxOrderPOService : ServiceImpl<WxOrderMapper, WxOrderPO>() {


    fun findByOutTradeNo(outTradeNo: String): WxOrderPO? {
        return getOne(KtQueryWrapper(WxOrderPO::class.java).eq(WxOrderPO::outTradeNo, outTradeNo))
    }

}