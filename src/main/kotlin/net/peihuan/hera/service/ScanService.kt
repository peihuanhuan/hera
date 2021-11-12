package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import mu.KotlinLogging
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.handler.click.DianfeiHandler
import net.peihuan.hera.persistent.service.PointsRecordPOService
import net.peihuan.hera.persistent.service.UserPointsPOService
import net.peihuan.hera.util.ZyUtil
import net.peihuan.hera.util.buildKfText
import net.peihuan.hera.util.completeALable
import net.peihuan.hera.util.completeMsgMenu
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


    fun handleQrsceneScan(wxMpXmlMessage: WxMpXmlMessage, qrscene: String) {
        if (qrscene.startsWith("电费")) {
            var content = cacheManage.getBizValue(BizConfigEnum.DIANFEI)
            content = content.completeMsgMenu(DianfeiHandler.reply)
            wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, content))
        } else if (qrscene.contains("腾讯视频")) {
            val channelId = channelService.getChannelOrCreate(wxMpXmlMessage.fromUser)
            val url = ZyUtil.buildOneProductUrl("C0002", channelId, zyProperties.appid)
            var content = """
                腾讯视频限时特惠！
                
                年卡104元，周卡5元
                
                <a>➜ 戳我进入购买页面</a>
            """.trimIndent()
            content = content.completeALable(url)
            wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, content))
        }
    }


}