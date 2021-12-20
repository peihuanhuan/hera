// package net.peihuan.hera.handler.click.member
//
// import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
// import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
// import net.peihuan.hera.config.ZyProperties
// import net.peihuan.hera.domain.CacheManage
// import net.peihuan.hera.handler.click.AbstractMenuAndMessageHandler
// import net.peihuan.hera.service.ChannelService
// import net.peihuan.hera.util.buildText
// import net.peihuan.hera.util.completeALable
// import org.springframework.stereotype.Component
//
// @Component
// // 双十一特价腾讯视频会员，临时类目
// class TencentMessageHandler(private val zyProperties: ZyProperties,
//                             private val channelService: ChannelService,
//                             private val cacheManage: CacheManage) : AbstractMenuAndMessageHandler() {
//
//     companion object {
//         const val reply = "双十一特价腾讯视频会员"
//     }
//
//     override fun receivedMessages(): List<String> {
//         TODO("Not yet implemented")
//     }
//
//
//     override fun handle(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
//         val channel = channelService.getChannelOrCreate(wxMpXmlMessage.fromUser).id
//         val url = "https://cdn.wxthe.com/life/#/pages/card/recharge?pcode=C0002&appid=${zyProperties.appid}&channel=$channel"
//         val content =  """
//             腾讯视频年卡限时 99 元
//
//             <a>戳我进入购买页面</a>
//         """.trimIndent()
//         return buildText(content.completeALable(url), wxMpXmlMessage)
//     }
//
//     override fun canHandleMenuClick(key: String): Boolean {
//         return false
//     }
//
// }