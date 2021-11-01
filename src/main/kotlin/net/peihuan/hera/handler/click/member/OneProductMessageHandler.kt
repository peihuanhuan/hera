package net.peihuan.hera.handler.click.member

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.handler.click.AbstractMenuHandler
import net.peihuan.hera.util.ZyUtil
import net.peihuan.hera.util.buildText
import net.peihuan.hera.util.completeALable
import net.peihuan.hera.util.toJsonObject
import org.springframework.stereotype.Component

@Component
class OneProductMessageHandler(private val zyProperties: ZyProperties,
                               private val cacheManage: CacheManage) : AbstractMenuHandler() {

    override fun canHandleMenuClick(key: String): Boolean {
        return key.startsWith("C")
    }

    override fun handleMenuClick(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val channel = ZyUtil.buildChannel(wxMpXmlMessage.fromUser)
        val url = "https://cdn.wxthe.com/life/#/pages/card/recharge?pcode=${wxMpXmlMessage.eventKey}&appid=${zyProperties.appid}&channel=$channel"

        val content = getProductName(wxMpXmlMessage.eventKey)

        return buildText(content.completeALable(url), wxMpXmlMessage)
    }

    fun getProductName(code: String): String {
        val bizValue = cacheManage.getBizValue(BizConfigEnum.MEMBER)
        val valueJsonObject = bizValue.toJsonObject()
        return if (valueJsonObject.has(code)) {
            valueJsonObject.get(code).asString
        } else {
            "没找到介绍"
        }
    }
}