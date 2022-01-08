package net.peihuan.hera.service

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest
import com.github.binarywang.wxpay.constant.WxPayConstants
import com.github.binarywang.wxpay.service.WxPayService
import mu.KotlinLogging
import net.peihuan.hera.config.WxPayProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.constants.OrderTypeEnum
import net.peihuan.hera.constants.PayStatusEnum
import net.peihuan.hera.constants.YYYYMMDDHHMMSS
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.persistent.po.WxOrderPO
import net.peihuan.hera.persistent.service.SubscribePOService
import net.peihuan.hera.persistent.service.UserPOService
import net.peihuan.hera.persistent.service.WxOrderPOService
import net.peihuan.hera.util.completeALable
import net.peihuan.hera.util.currentUserOpenid
import net.peihuan.hera.util.randomOutTradeNo
import net.peihuan.hera.util.replyKfMessage
import org.joda.time.format.DateTimeFormat
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class OrderService(private val userPOService: UserPOService,
                   private val wxPayService: WxPayService,
                   private val payProperties: WxPayProperties,
                   private val configService: ConfigService,
                   private val wxOrderPOService: WxOrderPOService,
                   private val cacheManage: CacheManage,
                   private val notifyService: NotifyService,
                   private val redPackageService: RedPackageService,
                   private val httpServletRequest: HttpServletRequest,
                   private val subscribePOService: SubscribePOService) {

    private val log = KotlinLogging.logger {}

    private val RED_PACKAGE_STYLE = 1

    fun order(type: Int): WxPayMpOrderResult {
        val typeEnum = OrderTypeEnum.getTypeEnum(type)!!

        val request = WxPayUnifiedOrderRequest()
        request.tradeType = WxPayConstants.TradeType.JSAPI
        request.deviceInfo = "WEB"
        val title = "阿烫-虎年兑换码"
        request.body = title
        request.outTradeNo = randomOutTradeNo()
        request.totalFee = 1
        request.openid = currentUserOpenid
        request.spbillCreateIp = httpServletRequest.remoteAddr
        request.notifyUrl =  "https://wx-test.peihuan.net/wx/portal/${payProperties.appId}/pay/callback"
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
            val redPackagePO = redPackageService.giveUpPackage(wxOrderPO.openid, RED_PACKAGE_STYLE)
            if (redPackagePO == null) {
                notifyService.notifyAdmin("！！！！！没有红包了！！！！！")
                return
            }
            var content = cacheManage.getBizValue(BizConfigEnum.BLESS) + "\n\n<a>➜ 戳我领取封面红包</a>"
            content = content.completeALable(redPackagePO.url)
            wxOrderPO.openid.replyKfMessage(content)
        }


    }
}