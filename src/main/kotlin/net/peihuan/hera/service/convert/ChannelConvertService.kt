package net.peihuan.hera.service.convert

import net.peihuan.hera.config.property.ZyProperties
import net.peihuan.hera.domain.Channel
import net.peihuan.hera.persistent.po.ChannelPO
import net.peihuan.hera.util.ZyUtil
import net.peihuan.hera.util.copyPropertiesTo
import org.springframework.stereotype.Component

@Component
class ChannelConvertService(private val zyProperties: ZyProperties,) {
    fun convert2Channel(channelPO: ChannelPO?) : Channel? {
        if (channelPO == null) {
            return null
        }
        val channel = channelPO.copyPropertiesTo<Channel>()
        channel.zyAllProductUrl = ZyUtil.buildAllProductUrl(channel.id, zyProperties.appid)
        return channel
    }
}