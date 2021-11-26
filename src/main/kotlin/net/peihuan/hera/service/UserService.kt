package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import me.chanjar.weixin.mp.bean.result.WxMpUser
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder
import net.peihuan.hera.config.ZyProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.constants.INVITER
import net.peihuan.hera.constants.StatusEnum
import net.peihuan.hera.constants.SubscribeSceneEnum
import net.peihuan.hera.domain.ActivityUser
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.handler.click.ExchangeMemberMessageHandler
import net.peihuan.hera.handler.click.SignClickMessageHandler
import net.peihuan.hera.handler.click.member.AllProductMessageHandler
import net.peihuan.hera.handler.click.member.TencentMessageHandler
import net.peihuan.hera.handler.click.waimai.ElmeWmHandler
import net.peihuan.hera.handler.click.waimai.MeituanWmHandler
import net.peihuan.hera.persistent.po.SubscribePO
import net.peihuan.hera.persistent.service.SubscribePOService
import net.peihuan.hera.persistent.service.UserInvitationShipService
import net.peihuan.hera.persistent.service.UserPOService
import net.peihuan.hera.persistent.service.UserTagPOService
import net.peihuan.hera.service.convert.UserConvertService
import net.peihuan.hera.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userPOService: UserPOService,
    private val wxMpService: WxMpService,
    private val userInvitationShipService: UserInvitationShipService,
    private val userTagPOService: UserTagPOService,
    private val userConvertService: UserConvertService,
    private val configService: ConfigService,
    private val userPointsService: UserPointsService,
    private val cacheManage: CacheManage,
    private val zyProperties: ZyProperties,
    private val scanService: ScanService,
    private val subscribePOService: SubscribePOService
) {

    @Transactional
    fun userSubscribeEvent(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val userWxInfo: WxMpUser = wxMpService.userService.userInfo(wxMpXmlMessage.fromUser, null)
        val dbUser = userPOService.getByOpenid(userWxInfo.openId)
        val newUser = userConvertService.convertToUserPO(userWxInfo)
        if (dbUser != null) {
            newUser.id = dbUser.id
        }
        userPOService.saveOrUpdate(newUser)
        addSubscribeRecord(userWxInfo)
        var replyContent =
            configService.getConfigWithCommon(WxMpConfigStorageHolder.get(), BizConfigEnum.SUBSCRIBE_REPLY_CONTENT)
                ?: "感谢关注"
        replyContent = replyContent.completeMsgMenu(
            MeituanWmHandler.reply,
            ElmeWmHandler.reply,
            AllProductMessageHandler.reply,
            SignClickMessageHandler.reply,
            TencentMessageHandler.reply
        )
        wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, replyContent))

        val qrscene = resolveQrscene(wxMpXmlMessage)

        if (dbUser == null) {
            presentPoints(wxMpXmlMessage)
            handleInviter(qrscene, userWxInfo.openId)
        }

        scanService.handleQrsceneScan(wxMpXmlMessage, qrscene)
        return null
    }

    private fun handleInviter(qrscene: String?, openid: String) {
        if (qrscene == null) {
            return
        }
        if (!qrscene.startsWith(INVITER)) {
            return
        }

        val activityUserJson = qrscene.removePrefix(INVITER)
        val activityInfo = activityUserJson.toBean<ActivityUser>()

        userInvitationShipService.addInvite(openid, activityInfo)
    }

    private fun resolveQrscene(wxMpXmlMessage: WxMpXmlMessage): String? {
        if (wxMpXmlMessage.event != "subscribe") {
            return null
        }
        return wxMpXmlMessage.eventKey.removePrefix("qrscene_")
    }



    private fun presentPoints(wxMpXmlMessage: WxMpXmlMessage) {
        val configPoints = cacheManage.getBizValue(BizConfigEnum.FIRST_SUBSCRIBE_PRESENT_POINTS) ?: "35"
        val firstSubscribe = """
                        首次关注，赠送小主 ${configPoints.toInt()} 积分~                
                        每天回复${buildMsgMenuUrl("签到", "【签到】")}可以获取积分哦
        
                        只要 $MIN_POINTS_CAN_EXCHANGE_MEMBER 积分就可以兑换会员啦
                        ${buildMsgMenuUrl(ExchangeMemberMessageHandler.receivedMessage, "➜ 戳我兑换会员")}
                    """.trimIndent()
        wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, firstSubscribe))
        userPointsService.addUserPoints(wxMpXmlMessage.fromUser, configPoints.toInt(), "首次关注赠送积分")
    }


    fun addSubscribeRecord(mpUser: WxMpUser) {
        val subscribePO = SubscribePO(
            openid = mpUser.openId,
            appid = WxMpConfigStorageHolder.get(),
            qrScene = mpUser.qrScene,
            qrSceneStr = mpUser.qrSceneStr,
            subscribeScene = SubscribeSceneEnum.valueOf(mpUser.subscribeScene).code,
            status = StatusEnum.ON.code
        )
        subscribePOService.save(subscribePO);
    }


    fun userUnSubscribeEvent(openid: String) {
        subscribePOService.unSubscribeRecord(openid)
    }


    fun addUserTag(openid: String, tagid: Long) {
        userTagPOService.addUserTag(openid, tagid)
    }

}