package net.peihuan.hera.persistent.service

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import net.peihuan.hera.persistent.mapper.EntPayOrderMapper
import net.peihuan.hera.persistent.po.EntPayOrderPO
import org.springframework.stereotype.Service

@Service
class EntPayOrderPOService : ServiceImpl<EntPayOrderMapper, EntPayOrderPO>() {

}