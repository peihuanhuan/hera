package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import mu.KotlinLogging
import net.peihuan.hera.persistent.mapper.ZyOrderMapper
import net.peihuan.hera.persistent.po.ZyOrderPO
import org.springframework.stereotype.Service

@Service
class ZyOrderPOService : ServiceImpl<ZyOrderMapper, ZyOrderPO>() {

    private val logger = KotlinLogging.logger {}

    fun queryOrdersByOutTradeNos(outTradeNos: List<String>):  List<ZyOrderPO> {
        if (outTradeNos.isEmpty()) {
            return list()
        }
        return list(KtQueryWrapper(ZyOrderPO::class.java).`in`(ZyOrderPO::outTradeNo, outTradeNos))
    }


}