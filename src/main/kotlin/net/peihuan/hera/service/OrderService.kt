package net.peihuan.hera.service

import com.github.binarywang.wxpay.bean.entpay.EntPayRequest
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult
import com.github.binarywang.wxpay.bean.request.WxPaySendRedpackRequest
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
import net.peihuan.hera.persistent.po.RedPackagePO
import net.peihuan.hera.persistent.po.WxOrderPO
import net.peihuan.hera.persistent.service.*
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
                   private val redPackagePOService: RedPackagePOService,
                   private val notifyService: NotifyService,
                   private val entPayOrderPOService: EntPayOrderPOService,
                   private val redPackageCoverService: RedPackageCoverService,
                   private val httpServletRequest: HttpServletRequest,
                   private val subscribePOService: SubscribePOService) {

    private val log = KotlinLogging.logger {}

    fun sendRedPackage(openid: String, amount: Int, zyOrderId :Long? = null) {
        val tradeNo = randomOutTradeNo()

        val redPackagePO = RedPackagePO(
            tradeNo = tradeNo,
            sendName = "阿烫",
            openid = openid,
            totalAmount = amount,
            wishing = "订单回馈，感谢您的支持。",
            actName = "订单回馈",
            remark = "无",
            zyOrderId = zyOrderId,
            sceneId = "PRODUCT_5"
        )
        redPackagePOService.save(redPackagePO)

        val req = WxPaySendRedpackRequest()
        req.mchBillNo = redPackagePO.tradeNo
        req.sendName = redPackagePO.sendName
        req.reOpenid = redPackagePO.openid
        req.totalAmount = redPackagePO.totalAmount
        req.totalNum = 1
        req.wishing = redPackagePO.wishing
        req.clientIp = httpServletRequest.remoteAddr
        req.actName = redPackagePO.actName
        req.remark = redPackagePO.remark
        req.sceneId = redPackagePO.sceneId

        val resp = wxPayService.redpackService.sendRedpack(req)
        redPackagePO.payTime = resp.sendTime
        redPackagePO.paymentNo = resp.sendListId
        redPackagePOService.updateById(redPackagePO)

    }

    fun determineBackAmount(profit: Int): Int {
        if (profit <= 30) {
            return 0
        }
        val backPercentStr = cacheManage.getBizValue(BizConfigEnum.ORDER_BACK_PERCENT, "35")
        val backPercent = backPercentStr.toInt().coerceAtLeast(0).coerceAtMost(90)
        return (profit * backPercent / 100).coerceAtLeast(30)
    }

    fun testBackMoney(openid: String, amount: Int, desc: String) {
        val backFen = amount.coerceAtLeast(1).coerceAtMost(1000)
        val randomOutTradeNo = randomOutTradeNo()

        val po = EntPayOrderPO(
            partnerTradeNo = randomOutTradeNo,
            openid = openid,
            amount = amount,
            description = desc,
            checkName = "NO_CHECK",
            zyOrderId = 0
        )
        entPayOrderPOService.save(po)

        val entPayRequest = EntPayRequest()
        entPayRequest.partnerTradeNo = po.partnerTradeNo
        entPayRequest.openid = openid
        entPayRequest.amount = po.amount
        entPayRequest.description = po.description
        entPayRequest.checkName = po.checkName
        entPayRequest.spbillCreateIp = httpServletRequest.remoteAddr
        val resp = wxPayService.entPayService.entPay(entPayRequest)
        log.info("企业支付响应 {}", resp)

        po.payTime = resp.paymentTime
        po.paymentNo = resp.paymentNo
        entPayOrderPOService.updateById(po)
    }

    // fun orderBackMoney(orderPO: ZyOrderPO) {
    //     val randomOutTradeNo = randomOutTradeNo()
    //
    //     val po = EntPayOrderPO(
    //         partnerTradeNo = randomOutTradeNo,
    //         openid = orderPO.openid!!,
    //         amount = backFen,
    //         description = "【${orderPO.name}】订单回馈",
    //         checkName = "NO_CHECK",
    //         zyOrderId = orderPO.id!!
    //     )
    //     entPayOrderPOService.save(po)
    //
    //     val entPayRequest = EntPayRequest()
    //     entPayRequest.partnerTradeNo = po.partnerTradeNo
    //     entPayRequest.openid = orderPO.openid
    //     entPayRequest.amount = po.amount
    //     entPayRequest.description = po.description
    //     entPayRequest.checkName = po.checkName
    //     entPayRequest.spbillCreateIp = httpServletRequest.remoteAddr
    //     val resp = wxPayService.entPayService.entPay(entPayRequest)
    //     log.info("企业支付响应 {}", resp)
    //
    //     po.payTime = resp.paymentTime
    //     po.paymentNo = resp.paymentNo
    //     entPayOrderPOService.updateById(po)
    // }

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
            redPackageCoverService.sendPackage(wxOrderPO.openid)
        }


    }
}