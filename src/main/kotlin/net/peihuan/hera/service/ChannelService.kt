package net.peihuan.hera.service

import com.baomidou.mybatisplus.core.toolkit.IdWorker
import me.chanjar.weixin.mp.api.WxMpService
import mu.KotlinLogging
import net.peihuan.hera.constants.ZyOrderSourceEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.domain.Channel
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.persistent.po.ChannelPO
import net.peihuan.hera.persistent.service.ChannelPOService
import net.peihuan.hera.persistent.service.PointsRecordPOService
import net.peihuan.hera.persistent.service.UserPOService
import net.peihuan.hera.service.convert.ChannelConvertService
import org.springframework.stereotype.Service

@Service
class ChannelService(private val channelPOService: ChannelPOService,
                     private val wxMpService: WxMpService,
                     private val userPOService: UserPOService,
                     private val cacheManage: CacheManage,
                     private val channelConvertService: ChannelConvertService,
                     private val pointsRecordPOService: PointsRecordPOService) {

    private val logger = KotlinLogging.logger {}

    fun getChannels(openid: String): List<Channel> {
        val channelPOs = channelPOService.getChannelPOs(openid = openid)
        return channelPOs.mapNotNull { channelConvertService.convert2Channel(it) }
    }

    fun getChannelOrCreate(openid: String, source: Int): Channel {
        userPOService.getByOpenid(openid) ?: throw BizException.buildBizException("用户不存在")
        val sourceEnum = ZyOrderSourceEnum.getSourceEnum(source) ?: throw BizException.buildBizException("枚举不存在")
        return getChannelOrCreate(openid, sourceEnum)
    }

    fun getChannelOrCreate(openid: String, source: ZyOrderSourceEnum = ZyOrderSourceEnum.BUY): Channel {
        var channel = channelPOService.getChannelPO(openid = openid, source = source)
        if (channel == null) {
            channel = ChannelPO(id = IdWorker.getId(), openid = openid, source = source)
            channelPOService.save(channel)
        }
        return channelConvertService.convert2Channel(channel)!!
    }

    fun getChannelById(channelId: Long) : ChannelPO? {
        return channelPOService.getById(channelId)
    }

}