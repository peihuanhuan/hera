package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import mu.KotlinLogging
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.persistent.service.PointsRecordPOService
import net.peihuan.hera.persistent.service.UserPointsPOService
import net.peihuan.hera.util.ZyUtil
import net.peihuan.hera.util.buildKfText
import net.peihuan.hera.util.completeALable
import org.springframework.stereotype.Service

@Service
class ScanService(
    private val userPointsPOService: UserPointsPOService,
    private val wxMpService: WxMpService,
    private val cacheManage: CacheManage,
    private val channelService: ChannelService,
    private val zyProperties: ZyProperties,
    private val pointsRecordPOService: PointsRecordPOService
) {

    private val logger = KotlinLogging.logger {}


    fun handleQrsceneScan(wxMpXmlMessage: WxMpXmlMessage, qrscene: String?) {
        if (qrscene == null) {
            return
        }
        val channelId = channelService.getChannelOrCreate(wxMpXmlMessage.fromUser)
        if (qrscene.contains("电费")) {
            var content = cacheManage.getBizValue(BizConfigEnum.DIANFEI)
            val url = ZyUtil.buildOneProductUrl("C0063", channelId, zyProperties.appid)
            content = content.completeALable(url)
            wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, content))
        } else if (qrscene.contains("腾讯视频")) {
            val url = ZyUtil.buildOneProductUrl("C0002", channelId, zyProperties.appid)
            var content = """
                腾讯视频限时特惠！
                
                年卡104元，周卡5元
                
                <a>➜ 戳我进入购买页面</a>
            """.trimIndent()
            content = content.completeALable(url)
            wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, content))
        } else if (qrscene.contains("喜马拉雅")) {
            val url = ZyUtil.buildOneProductUrl("C0025", channelId, zyProperties.appid)
            var content = """
                喜马拉雅限时特惠！
                
                年卡97元，周卡2元
                
                <a>➜ 戳我进入购买页面</a>
            """.trimIndent()
            content = content.completeALable(url)
            wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, content))
        } else if (qrscene.uppercase().contains("芒果TV")) {
            val url = ZyUtil.buildOneProductUrl("C0016", channelId, zyProperties.appid)
            var content = """
                芒果TV限时特惠！
                
                年卡85元，周卡5.9元
                
                <a>➜ 戳我进入购买页面</a>
            """.trimIndent()
            content = content.completeALable(url)
            wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, content))
        }
    }


}