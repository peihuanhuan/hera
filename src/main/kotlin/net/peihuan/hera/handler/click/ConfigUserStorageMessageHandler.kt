package net.peihuan.hera.handler.click

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import net.peihuan.hera.config.property.HeraProperties
import net.peihuan.hera.constants.FILE_STORAGE_PLATFORM
import net.peihuan.hera.constants.FILE_STORAGE_PLATFORM_ALI
import net.peihuan.hera.constants.FILE_STORAGE_PLATFORM_BAIDU
import net.peihuan.hera.persistent.service.UserPOService
import net.peihuan.hera.service.ConfigService
import net.peihuan.hera.util.replyKfMessage
import org.springframework.stereotype.Component

@Component
class ConfigUserStorageMessageHandler(private val wxMpService: WxMpService,
                                      private val userService: UserPOService,
                                      private val configService: ConfigService,
                                      private val heraProperties: HeraProperties
) : AbstractMessageHandler {

    override fun receivedMessages(): List<String> {
        return listOf("优先")
    }

    override fun canHandle(message: String): Boolean {
        return message.contains("优先")
    }

    override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val value = if (wxMpXmlMessage.content.contains("阿里")) {
            FILE_STORAGE_PLATFORM_ALI
        } else if (wxMpXmlMessage.content.contains("百度")) {
            FILE_STORAGE_PLATFORM_BAIDU
        } else ""
        if (value.isBlank()) {
            return null
        }
        configService.updateUserConfig(wxMpXmlMessage.fromUser, FILE_STORAGE_PLATFORM, value)
        if (value == FILE_STORAGE_PLATFORM_ALI) {
            wxMpXmlMessage.replyKfMessage("已切换为【优先使用阿里云盘】")
        }
        if (value == FILE_STORAGE_PLATFORM_BAIDU) {
            wxMpXmlMessage.replyKfMessage("已切换为【优先使用百度云盘】")
        }
        return null
    }

}