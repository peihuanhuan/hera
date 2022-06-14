// package net.peihuan.hera.service.storage
//
// import mu.KotlinLogging
// import net.peihuan.baiduPanSDK.domain.dto.CreateResponseDTO
// import net.peihuan.baiduPanSDK.service.BaiduService
// import net.peihuan.baiduPanSDK.service.impl.BaiduOAuthConfigServiceImpl
// import net.peihuan.hera.config.WxMysqlOps
// import net.peihuan.hera.domain.CacheManage
// import net.peihuan.hera.persistent.service.LockPOService
// import net.peihuan.hera.service.BlackKeywordService
// import net.peihuan.hera.service.ConfigService
// import net.peihuan.hera.service.NotifyService
// import org.springframework.stereotype.Service
// import java.io.File
// import javax.annotation.PostConstruct
//
//
// @Service
// class BaiduPanService(
//     private val notifyService: NotifyService,
//     private val baiduService: BaiduService,
//     private val blackKeywordService: BlackKeywordService,
//     private val cacheManage: CacheManage,
//     val configService: ConfigService,
//     val lockPOService: LockPOService
// ) {
//
//     private val log = KotlinLogging.logger {}
//
//     companion object val ROOT_USER_ID = "1"
//
//     @PostConstruct
//     fun setToken() {
//         val wxMysqlOps = WxMysqlOps(configService, lockPOService)
//         val storage = BaiduOAuthConfigServiceImpl(wxMysqlOps)
//         baiduService.setConfigStorage(storage)
//     }
//
//     fun upload(path: String, file: File): CreateResponseDTO? {
//         return baiduService.getPanService().uploadFile(ROOT_USER_ID, path, file)
//     }
//
//
//
// }
