package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder
import mu.KotlinLogging
import net.peihuan.hera.constants.OrderSourceEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.persistent.po.ChannelPO
import net.peihuan.hera.persistent.service.ChannelPOService
import net.peihuan.hera.persistent.service.PointsRecordPOService
import org.springframework.stereotype.Service

@Service
class ChannelService(private val channelPOService: ChannelPOService,
                     private val wxMpService: WxMpService,
                     private val cacheManage: CacheManage,
                     private val pointsRecordPOService: PointsRecordPOService) {

    private val logger = KotlinLogging.logger {}

    fun getChannelOrCreate(openid: String, source: OrderSourceEnum = OrderSourceEnum.BUY): Long {
        val appid = WxMpConfigStorageHolder.get()
        val channel = channelPOService.getChannel(openid = openid, appid = appid, source = source)
        if (channel != null) {
            return channel.id!!
        }

        val po = ChannelPO(openid = openid, appid = appid, source = source)
        channelPOService.save(po)
        return po.id!!
    }

    fun getChannelById(channelId: Long) : ChannelPO? {
        return channelPOService.getById(channelId)
    }

}