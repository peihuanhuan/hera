package net.peihuan.hera.service

import com.github.binarywang.wxpay.bean.entpay.EntPayRequest
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest
import com.github.binarywang.wxpay.constant.WxPayConstants
import com.github.binarywang.wxpay.service.WxPayService
import mu.KotlinLogging
import net.peihuan.hera.config.property.WxPayProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.constants.OrderTypeEnum
import net.peihuan.hera.constants.PayStatusEnum
import net.peihuan.hera.constants.YYYYMMDDHHMMSS
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.persistent.po.EntPayOrderPO
import net.peihuan.hera.persistent.po.WxOrderPO
import net.peihuan.hera.persistent.po.ZyOrderPO
import net.peihuan.hera.persistent.service.EntPayOrderPOService
import net.peihuan.hera.persistent.service.SubscribePOService
import net.peihuan.hera.persistent.service.UserPOService
import net.peihuan.hera.persistent.service.WxOrderPOService
import net.peihuan.hera.util.currentUserOpenid
import net.peihuan.hera.util.randomOutTradeNo
import org.joda.time.format.DateTimeFormat
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
class OrderService(private val userPOService: UserPOService,
                   private val wxPayService: WxPayService,
                   private val payProperties: WxPayProperties,
                   private val configService: ConfigService,
                   private val wxOrderPOService: WxOrderPOService,
                   private val cacheManage: CacheManage,
                   private val notifyService: NotifyService,
                   private val entPayOrderPOService: EntPayOrderPOService,
                   private val redPackageService: RedPackageService,
                   private val httpServletRequest: HttpServletRequest,
                   private val subscribePOService: SubscribePOService) {

    private val log = KotlinLogging.logger {}

    fun orderBackMoney(orderPO: ZyOrderPO) {
        val backPercentStr = cacheManage.getBizValue(BizConfigEnum.ORDER_BACK_PERCENT, "30")
        val backPercent = backPercentStr.toInt().coerceAtLeast(0).coerceAtMost(90)
        val backFen = ((orderPO.incomeMoney?:0) * backPercent / 100).coerceAtLeast(1)
        val randomOutTradeNo = randomOutTradeNo()

        val po = EntPayOrderPO(
            partnerTradeNo = randomOutTradeNo,
            openid = orderPO.openid!!,
            amount = backFen,
            description = "【${orderPO.name}】订单回馈",
            checkName = "NO_CHECK",
            zyOrderId = orderPO.id!!
        )
        entPayOrderPOService.save(po)

        val entPayRequest = EntPayRequest()
        entPayRequest.partnerTradeNo = po.partnerTradeNo
        entPayRequest.openid = orderPO.openid
        entPayRequest.amount = po.amount
        entPayRequest.description = po.description
        entPayRequest.checkName = po.checkName
        val resp = wxPayService.entPayService.entPay(entPayRequest)
        log.info("企业支付响应 {}", resp)

        po.payTime = resp.paymentTime
        po.paymentNo = resp.paymentNo
        entPayOrderPOService.updateById(po)
    }

    fun order(type: Int): WxPayMpOrderResult {
        val typeEnum = OrderTypeEnum.getTypeEnum(type)!!

        val request = WxPayUnifiedOrderRequest()
        request.tradeType = WxPayConstants.TradeType.JSAPI
        request.deviceInfo = "WEB"
        val title = "阿烫-积分购买"
        request.body = title
        request.outTradeNo = randomOutTradeNo()
        request.totalFee = Random().nextInt(70) + 30
        request.openid = currentUserOpenid
        request.spbillCreateIp = httpServletRequest.remoteAddr
        request.notifyUrl =  "https://wx.peihuan.net/wx/portal/${payProperties.appId}/pay/callback"
        val createOrder = wxPayService.createOrder<WxPayMpOrderResult>(request)

        val orderPO = WxOrderPO(
            title = title,
            openid = currentUserOpenid,
            outTradeNo = request.outTradeNo,
            totalFee = request.totalFee,
            payFee = request.totalFee,
            status = PayStatusEnum.DEFAULT,
            type = typeEnum,
        )
        wxOrderPOService.save(orderPO)

        return createOrder
    }

    fun handlePayCallback(result: WxPayOrderNotifyResult) {
        log.info { "微信支付回调 $result" }
        val wxOrderPO = wxOrderPOService.findByOutTradeNo(result.outTradeNo)
        if (wxOrderPO == null) {
            log.warn { "不存在该outTradeNo ${result.outTradeNo}" }
            return
        }
        if (wxOrderPO.status == PayStatusEnum.SUCCESS) {
            log.info { "已成功处理 $result" }
            return
        }
        if (wxOrderPO.payFee != result.totalFee) {
            log.error { "支付金额不一致 ${result.outTradeNo}" }
            return
        }
        wxOrderPO.payTime = DateTimeFormat.forPattern(YYYYMMDDHHMMSS).parseDateTime(result.timeEnd).toDate()
        wxOrderPO.status = PayStatusEnum.SUCCESS
        wxOrderPO.transactionId = result.transactionId
        wxOrderPO.updateTime = null
        wxOrderPOService.updateById(wxOrderPO)

        if (wxOrderPO.type == OrderTypeEnum.RED_PACKAGE) {
            redPackageService.sendPackage(wxOrderPO.openid)
        }


    }
}