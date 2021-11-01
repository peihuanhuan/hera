package net.peihuan.hera.handler.wxmp

import me.chanjar.weixin.mp.api.WxMpMessageHandler
import mu.KotlinLogging

abstract class AbstractHandler : WxMpMessageHandler {
    val log = KotlinLogging.logger {}
}