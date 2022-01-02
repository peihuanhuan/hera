package net.peihuan.hera.task

import me.chanjar.weixin.mp.api.WxMpService
import mu.KotlinLogging
import net.peihuan.hera.config.WxMpProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.constants.TaskStatusEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.feign.service.ZyService
import net.peihuan.hera.persistent.service.BilibiliAudioTaskPOService
import net.peihuan.hera.persistent.service.ZyOrderPOService
import net.peihuan.hera.service.ChannelService
import net.peihuan.hera.service.NotifyService
import net.peihuan.hera.service.UserPointsService
import net.peihuan.hera.service.UserService
import org.joda.time.DateTime
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class CheckBilibiliTask(
    val zyService: ZyService,
    val bilibiliAudioTaskPOService: BilibiliAudioTaskPOService,
    val wxMpService: WxMpService,
    val userPointsService: UserPointsService,
    val userService: UserService,
    val channelService: ChannelService,
    val cacheManage: CacheManage,
    val notifyService: NotifyService,
    val wxMpProperties: WxMpProperties,
    val zyOrderPOService: ZyOrderPOService
) {

    private val log = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 180_000)
    fun scheduled() {
        val minutes = cacheManage.getBizValue(BizConfigEnum.ALARM_TIME, "5").toInt()
        val tasks = bilibiliAudioTaskPOService.findByStatus(TaskStatusEnum.DEFAULT)
        tasks.forEach { task ->
            if (task.createTime!!.before(DateTime.now().minusMinutes(minutes).toDate())) {
                notifyService.notifyTaskTooLong(task)
            }
        }
    }

}