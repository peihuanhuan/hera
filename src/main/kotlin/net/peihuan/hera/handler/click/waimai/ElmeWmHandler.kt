// package net.peihuan.hera.handler.click.waimai
//
// import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
// import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
// import net.peihuan.hera.config.ZyProperties
// import net.peihuan.hera.constants.BizConfigEnum
// import net.peihuan.hera.domain.CacheManage
// import net.peihuan.hera.handler.click.AbstractMenuAndMessageHandler
// import net.peihuan.hera.util.buildMsgMenuUrl
// import net.peihuan.hera.util.buildText
// import org.springframework.stereotype.Component
//
//
// @Component
// class ElmeWmHandler(
//         private val zyProperties: ZyProperties,
//         private val cacheManage: CacheManage,
// ) : AbstractMenuAndMessageHandler() {
//     companion object {
//         const val reply = "【点我】饿了么红包"
//         const val showMessage = "【点我】饿了么大额外卖红包"
//     }
//
//     override fun showMsg(): String {
//         return showMessage
//     }
//
//     override fun reply(): String {
//         return reply
//     }
//
//     override fun canHandleMenuClick(key: String): Boolean {
//         return key == "elme"
//     }
//
//     override fun handleMessage(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage {
//         var elmeContent = cacheManage.getBizValue(BizConfigEnum.ELME)
//         elmeContent += buildMsgMenuUrl(MeituanWmHandler.reply, "还有美团红包未领取哦~")
//         return buildText(elmeContent, wxMpXmlMessage)
//     }
// }