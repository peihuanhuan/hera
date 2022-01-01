// package net.peihuan.hera.task
//
// import me.chanjar.weixin.mp.api.WxMpService
// import mu.KotlinLogging
// import net.peihuan.hera.feign.service.ZyService
// import net.peihuan.hera.persistent.service.SubscribePOService
// import net.peihuan.hera.persistent.service.UserPOService
// import net.peihuan.hera.persistent.service.ZyOrderPOService
// import net.peihuan.hera.service.UserService
// import net.peihuan.hera.service.convert.UserConvertService
// import net.peihuan.hera.util.toJson
// import org.springframework.scheduling.annotation.Scheduled
// import org.springframework.stereotype.Component
//
//
// @Component
// class UpdateUserInfoTask(val zyService: ZyService,
//                          val wxMpService: WxMpService,
//                          val userService: UserService,
//                          val userPOService: UserPOService,
//                          val subscribePOService: SubscribePOService,
//                          val userConvertService: UserConvertService,
//                          val zyOrderPOService: ZyOrderPOService) {
//
//     private val log = KotlinLogging.logger {}
//
//     @Scheduled(cron = "0 0 3 * * ?")
//     fun scheduled() {
//         val openids = subscribePOService.getSubscribeOpenids()
//         openids.forEach { updateUser(it) }
//     }
//
//     private fun updateUser(openid: String) {
//         val wxMpUser = wxMpService.userService.userInfo(openid)
//         val dbUser = userPOService.getByOpenid(openid) ?: return
//         val newUser = userConvertService.convertToUserPO(wxMpUser)
//         newUser.id = dbUser.id
//         // 获取到的 appid 是 default，清除不设置
//         newUser.appid = null
//         userPOService.updateById(newUser)
//         log.info { "更新用户信息 ${wxMpUser.toJson()}" }
//     }
// }