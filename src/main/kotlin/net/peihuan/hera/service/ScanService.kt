package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import mu.KotlinLogging
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.handler.click.DianfeiHandler
import net.peihuan.hera.persistent.service.PointsRecordPOService
import net.peihuan.hera.persistent.service.UserPointsPOService
import net.peihuan.hera.util.buildKfText
import net.peihuan.hera.util.completeMsgMenu
import org.springframework.stereotype.Service

@Service
class ScanService(private val userPointsPOService: UserPointsPOService,
                  private val wxMpService: WxMpService,
                  private val cacheManage: CacheManage,
                  private val pointsRecordPOService: PointsRecordPOService) {

    private val logger = KotlinLogging.logger {}


    fun handleQrsceneScan(wxMpXmlMessage: WxMpXmlMessage, qrscene: String) {
        if (qrscene.startsWith("电费")) {
            var content = cacheManage.getBizValue(BizConfigEnum.DIANFEI)
            content = content.completeMsgMenu(DianfeiHandler.reply)
            wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, content))
        }
    }


}