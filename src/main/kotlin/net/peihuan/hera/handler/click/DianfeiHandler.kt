// package net.peihuan.hera.handler.click
//
// import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
// import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
// import net.peihuan.hera.config.property.ZyProperties
// import net.peihuan.hera.domain.CacheManage
// import net.peihuan.hera.service.ChannelService
// import net.peihuan.hera.util.buildText
// import net.peihuan.hera.util.completeALable
// import org.springframework.stereotype.Component
//
//
// @Component
// class DianfeiHandler(
//         private val zyProperties: ZyProperties,
//         private val channelService: ChannelService,
//         private val cacheManage: CacheManage,
// ) : AbstractMenuAndMessageHandler() {
//
//     companion object {
//         const val reply = "优惠电费充值"
//     }
//
//     override fun showMsg(): String {
//         return "➜ 戳我了解优惠电费"
//     }
//
//     override fun reply(): String {
//         return reply
//     }
//
//     override fun canHandleMenuClick(key: String): Boolean {
//         return key == "dianfei"
//     }
//
//     override fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage {
//         val channel = channelService.getChannelOrCreate(wxMpXmlMessage.fromUser).id
//         val url = "https://cdn.wxthe.com/life/#/pages/card/recharge?pcode=C0063&appid=${zyProperties.appid}&channel=$channel"
//         val huafeiContent = "<a>戳我进入优惠电费充值页面</a>"
//         return buildText(huafeiContent.completeALable(url), wxMpXmlMessage)
//     }
// }