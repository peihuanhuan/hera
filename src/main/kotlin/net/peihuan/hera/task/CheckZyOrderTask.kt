package net.peihuan.hera.task

import me.chanjar.weixin.mp.api.WxMpService
import mu.KotlinLogging
import net.peihuan.hera.config.WxMpProperties
import net.peihuan.hera.constants.YYYY_MM_DD
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.feign.dto.ZyOrder
import net.peihuan.hera.feign.service.ZyService
import net.peihuan.hera.persistent.po.ZyOrderPO
import net.peihuan.hera.persistent.service.ZyOrderPOService
import net.peihuan.hera.service.NotifyService
import net.peihuan.hera.service.UserPointsService
import net.peihuan.hera.util.ZyUtil
import net.peihuan.hera.util.toJson
import org.joda.time.DateTime
import org.springframework.beans.BeanUtils
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class CheckZyOrderTask(val zyService: ZyService,
                       val wxMpService: WxMpService,
                       val userPointsService: UserPointsService,
                       val notifyService: NotifyService,
                       val wxMpProperties: WxMpProperties,
                       val zyOrderPOService: ZyOrderPOService) {

    private val log = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 60_000)
    fun scheduled() {

        val to = DateTime.now().withTimeAtStartOfDay()
        val from = to.minusDays(7)
        val zyOrders = queryZyOrders(from, to)


        // 已完成订单
        val finishedOrders = zyOrders.filter { it.status == 3 }

        val outTradeNos = finishedOrders.map { it.out_trade_no ?: "" }
        val existOrderTradeNos = zyOrderPOService.queryOrdersByOutTradeNos(outTradeNos).map { it.outTradeNo ?: "" }

        val newOrders = finishedOrders.filter { !existOrderTradeNos.contains(it.out_trade_no) }

        val newOrderPOs = newOrders.map {
            val po = ZyOrderPO()
            BeanUtils.copyProperties(it.goods!!, po)
            BeanUtils.copyProperties(it, po)
            po.outTradeNo = it.out_trade_no
            po.incomeMoney = it.income_money
            po.openid = ZyUtil.getChannelOpenid(it.channel ?: "")
            po
        }

        if (newOrderPOs.isNotEmpty()) {
            zyOrderPOService.saveBatch(newOrderPOs)
            log.info { "新增了 ${newOrderPOs.size} 个已完成订单， ${newOrderPOs.toJson()}" }
        }

        newOrderPOs.forEach {
            val points = (it.incomeMoney ?: 1).coerceAtMost(1000)
            userPointsService.addUserPoints(it.openid ?: "null", points, "订单返现【${it.name}】")
            // val content = """
            //     小主，您的订单【${it.name}】已完成，赠送您 $points 积分哦~
            // """.trimIndent()
            notifyService.notifyOrderStatusToUser(it, points)
            // wxMpService.kefuService.sendKefuMessage(buildKfText(it.openid ?: "", content))
            notifyService.notifyOrderStatusToAdmin(it)
        }

        wxMpService.userTagService.batchTagging(wxMpProperties.tags.hasZyMemberOrder, newOrderPOs.map { it.openid }.toTypedArray())
        // todo userTagPO;
    }

    private fun queryZyOrders(from: DateTime, to: DateTime): List<ZyOrder> {
        val pageSize = 10
        var page = 1
        val allZyOrders = mutableListOf<ZyOrder>()
        var orders: List<ZyOrder>
        do {
            orders = queryZyOrders(from, to, page, pageSize)
            allZyOrders.addAll(orders)
            page++
        } while (orders.size == pageSize)
        return allZyOrders
    }


    private fun queryZyOrders(from: DateTime, to: DateTime, page: Int, size: Int): List<ZyOrder> {
        val zyResponse = zyService.queryH5Orders(from.toString(YYYY_MM_DD), to.toString(YYYY_MM_DD), page, size)
        if ((zyResponse?.code ?: 0) != 200) {
            throw BizException.buildBizException("请求众佣订单接口失败")
        }
        return zyResponse?.data?.list ?: listOf()
    }
}