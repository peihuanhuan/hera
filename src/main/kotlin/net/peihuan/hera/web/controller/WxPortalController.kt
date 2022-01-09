package net.peihuan.hera.web.controller

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse
import com.github.binarywang.wxpay.service.WxPayService
import me.chanjar.weixin.mp.api.WxMpMessageRouter
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import mu.KotlinLogging
import net.peihuan.hera.service.OrderService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/wx/portal/{appid}")
class WxPortalController {

    private val log = KotlinLogging.logger() {}
    @Autowired
    private lateinit var wxService: WxMpService
    @Autowired
    private lateinit var messageRouter: WxMpMessageRouter
    @Autowired
    private lateinit var orderService: OrderService
    @Autowired
    private lateinit var payService: WxPayService

    @PostMapping("/pay/callback")
    fun payResult(@RequestBody xmlData: String): String{
        val parseOrderNotifyResult = payService.parseOrderNotifyResult(xmlData)
        orderService.handlePayCallback(parseOrderNotifyResult)
        return WxPayNotifyResponse.success("成功")
    }

    @GetMapping(produces = ["text/plain;charset=utf-8"])
    fun authGet(@PathVariable appid: String?,
                @RequestParam(name = "signature", required = false) signature: String?,
                @RequestParam(name = "timestamp", required = false) timestamp: String?,
                @RequestParam(name = "nonce", required = false) nonce: String?,
                @RequestParam(name = "echostr", required = false) echostr: String?): String? {
        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature, timestamp, nonce, echostr)

        require(!StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) { "请求参数非法，请核实!" }
        require(this.wxService.switchover(appid)) { String.format("未找到对应appid=[%s]的配置，请核实！", appid) }
        return if (wxService.checkSignature(timestamp, nonce, signature)) {
            echostr
        } else {
            "非法请求"
        }
    }


    @PostMapping(produces = ["application/xml; charset=UTF-8"])
    fun post(@PathVariable appid: String?,
             @RequestBody requestBody: String?,
             @RequestParam("signature") signature: String?,
             @RequestParam("timestamp") timestamp: String?,
             @RequestParam("nonce") nonce: String?,
             @RequestParam("openid") openid: String?,
             @RequestParam(name = "encrypt_type", required = false) encType: String?,
             @RequestParam(name = "msg_signature", required = false) msgSignature: String?): String? {

        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ", openid, signature, encType, msgSignature, timestamp, nonce, requestBody)
        require(wxService.switchover(appid)) { String.format("未找到对应appid=[%s]的配置，请核实！", appid) }
        require(wxService.checkSignature(timestamp, nonce, signature)) { "非法请求，可能属于伪造的请求！" }
        var out: String? = null
        if (encType == null) {
            // 明文传输的消息
            val inMessage = WxMpXmlMessage.fromXml(requestBody)
            val outMessage = route(inMessage) ?: return ""
            out = outMessage.toXml()
        } else if ("aes".equals(encType, ignoreCase = true)) {
            // aes加密的消息
            val inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.wxMpConfigStorage,
                    timestamp, nonce, msgSignature)
            log.debug("\n消息解密后内容为：\n{} ", inMessage.toString())
            val outMessage = route(inMessage) ?: return ""
            out = outMessage.toEncryptedXml(wxService.wxMpConfigStorage)
        }
        log.info ("\n组装回复信息：{}", out)
        return out
    }

    fun route(message: WxMpXmlMessage): WxMpXmlOutMessage? {
        try {
            return messageRouter.route(message)
        } catch (e: Exception) {
            log.error("路由消息时出现异常！", e)
        }
        return null
    }

}