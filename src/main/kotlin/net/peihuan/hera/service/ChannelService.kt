package net.peihuan.hera.service

import com.baomidou.mybatisplus.core.toolkit.IdWorker
import me.chanjar.weixin.mp.api.WxMpService
import mu.KotlinLogging
import net.peihuan.hera.constants.OrderSourceEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.domain.Channel
import net.peihuan.hera.persistent.po.ChannelPO
import net.peihuan.hera.persistent.service.ChannelPOService
import net.peihuan.hera.persistent.service.PointsRecordPOService
import net.peihuan.hera.service.convert.ChannelConvertService
import org.springframework.stereotype.Service

@Service
class ChannelService(private val channelPOService: ChannelPOService,
                     private val wxMpService: WxMpService,
                     private val cacheManage: CacheManage,
                     private val channelConvertService: ChannelConvertService,
                     private val pointsRecordPOService: PointsRecordPOService) {

    private val logger = KotlinLogging.logger {}

    fun getChannels(openid: String): List<Channel> {
        val channelPOs = channelPOService.getChannelPOs(openid = openid)
        return channelPOs.mapNotNull { channelConvertService.convert2Channel(it) }
    }

    fun createChannel() {

    }

    fun getChannelOrCreate(openid: String, source: OrderSourceEnum = OrderSourceEnum.BUY): Long {
        val channel = channelPOService.getChannelPO(openid = openid, source = source)
        if (channel != null) {
            return channel.id
        }

        val po = ChannelPO(id = IdWorker.getId(), openid = openid, source = source)
        channelPOService.save(po)
        return po.id
    }

    fun getChannelById(channelId: Long) : ChannelPO? {
        return channelPOService.getById(channelId)
    }

}