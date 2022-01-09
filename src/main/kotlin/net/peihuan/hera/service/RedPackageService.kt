package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import mu.KotlinLogging
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.persistent.po.RedPackagePO
import net.peihuan.hera.persistent.service.PointsRecordPOService
import net.peihuan.hera.persistent.service.RedPackagePOService
import net.peihuan.hera.persistent.service.UserPointsPOService
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.Charset

@Service
class RedPackageService(
    private val userPointsPOService: UserPointsPOService,
    private val wxMpService: WxMpService,
    private val redPackagePOService: RedPackagePOService,
    private val notifyService: NotifyService,
    private val cacheManage: CacheManage,
    private val channelService: ChannelService,
    private val zyProperties: ZyProperties,
    private val pointsRecordPOService: PointsRecordPOService
) {

    private val logger = KotlinLogging.logger {}

    fun giveUpPackage(openid: String, style: Int): RedPackagePO? {
        val notGiveupPackages = redPackagePOService.findNotGiveupPackages(style)
        val alarmCount = cacheManage.getBizValue(BizConfigEnum.RED_PACKAGE_NOT_ENOUGH, "20").toInt()
        if (notGiveupPackages.size < alarmCount) {
            notifyService.notifyAdmin("红包数量不足了，还剩 ${notGiveupPackages.size} 个，style: $style")
        }
        notGiveupPackages.forEach {
            val success = redPackagePOService.giveUp(it.id!!, openid)
            if (success) {
                return it
            }
            logger.info { "尝试 ${it.id}，失败" }
        }

        return null
    }

    fun saveBatchRedPackage(file: File, style: Int): Int {
        if (!file.exists()) {
            logger.error { "文件不存在 ${file.absolutePath}" }
            return 0
        }
        val existUrls = redPackagePOService.list().map { it.url }.toSet()
        val urls = FileUtils.readLines(file, Charset.forName("utf-8"))
        urls.removeAll(existUrls)
        val pos = urls.map { url ->
            RedPackagePO(url = url, giveUp = false, style = style)
        }
        if (pos.isNotEmpty()) {
            redPackagePOService.saveBatch(pos)
            notifyService.notifyAdmin("添加 ${pos.size} 个红包， style 为 $style")
        }
        return pos.size
    }

}