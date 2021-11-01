package net.peihuan.hera.config

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType
import me.chanjar.weixin.mp.api.WxMpMessageRouter
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.constant.WxMpEventConstants
import me.chanjar.weixin.mp.constant.WxMpEventConstants.CustomerService
import net.peihuan.hera.handler.wxmp.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(WxMpProperties::class)
class WxMpHandlerConfiguration(
        val logHandler: LogHandler,
        val nullHandler: NullHandler,
        val kfSessionHandler: KfSessionHandler,
        val storeCheckNotifyHandler: StoreCheckNotifyHandler,
        val locationHandler: LocationHandler,
        val menuHandler: MenuHandler,
        val msgHandler: MsgHandler,
        val unsubscribeHandler: UnsubscribeHandler,
        val subscribeHandler: SubscribeHandler,
        val scanHandler: ScanHandler,
) {


    @Bean
    fun messageRouter(wxMpService: WxMpService?): WxMpMessageRouter {
        val newRouter = WxMpMessageRouter(wxMpService)

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(logHandler).next()

        // 接收客服会话管理事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(CustomerService.KF_CREATE_SESSION)
                .handler(kfSessionHandler).end()
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(CustomerService.KF_CLOSE_SESSION)
                .handler(kfSessionHandler).end()
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(CustomerService.KF_SWITCH_SESSION)
                .handler(kfSessionHandler).end()

        // 门店审核事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(WxMpEventConstants.POI_CHECK_NOTIFY).handler(storeCheckNotifyHandler).end()

        // 自定义菜单事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(WxConsts.EventType.CLICK).handler(menuHandler).end()

        // 点击菜单连接事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(WxConsts.EventType.VIEW).handler(nullHandler).end()

        // 关注事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(WxConsts.EventType.SUBSCRIBE).handler(subscribeHandler).end()

        // 取消关注事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(WxConsts.EventType.UNSUBSCRIBE).handler(unsubscribeHandler).end()

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(WxConsts.EventType.LOCATION).handler(locationHandler).end()

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(XmlMsgType.LOCATION).handler(locationHandler).end()

        // 扫码事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(WxConsts.EventType.SCAN).handler(scanHandler).end()

        // 默认
        newRouter.rule().async(false).handler(msgHandler).end()
        return newRouter
    }
}