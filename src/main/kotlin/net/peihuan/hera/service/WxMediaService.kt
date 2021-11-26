package net.peihuan.hera.service

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.mp.api.WxMpService
import net.peihuan.hera.persistent.service.SubscribePOService
import net.peihuan.hera.persistent.service.UserPOService
import org.springframework.stereotype.Service

@Service
class WxMediaService(private val userPOService: UserPOService,
                     private val wxMpService: WxMpService,
                     private val configService: ConfigService,
                     private val subscribePOService: SubscribePOService) {

    fun uploadPic() {
        val materialFileBatchGet = wxMpService.materialService.materialFileBatchGet(WxConsts.MaterialType.IMAGE, 0, 10)
    }
}