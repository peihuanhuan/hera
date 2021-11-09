package net.peihuan.hera.persistent.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import net.peihuan.hera.persistent.po.SubscribePO
import org.apache.ibatis.annotations.Select


interface SubscribeMapper : BaseMapper<SubscribePO> {

    @Select("select DISTINCT openid from `subscribe` where status = 1")
    fun findSubscribeOpenids() : List<String>

}