package net.peihuan.hera.persistent.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import net.peihuan.hera.persistent.po.RedPackageCoverPO


interface RedPackageCoverMapper : BaseMapper<RedPackageCoverPO> {

    fun tryGiveup(id: Long, openid: String) : Int

}