package net.peihuan.hera.service

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import me.chanjar.weixin.mp.bean.result.WxMpUser
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder
import net.peihuan.baiduPanSDK.service.BaiduService
import net.peihuan.hera.config.property.ZyProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.constants.StatusEnum
import net.peihuan.hera.constants.SubscribeSceneEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.domain.User
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.handler.click.ActivityMessageHandler
import net.peihuan.hera.handler.click.ExchangeMemberMessageHandler
import net.peihuan.hera.handler.click.RedPackageMessageHandler
import net.peihuan.hera.handler.click.SignClickMessageHandler
import net.peihuan.hera.handler.click.member.AllProductMessageHandler
import net.peihuan.hera.handler.click.member.OneProductMessageHandler
import net.peihuan.hera.persistent.po.SubscribePO
import net.peihuan.hera.persistent.po.UserPO
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
    private val redPackageService: RedPackageService,
    private val userConvertService: UserConvertService,
    private val configService: ConfigService,
    private val channelService: ChannelService,
    private val userPointsService: UserPointsService,
    private val allProductMessageHandler: AllProductMessageHandler,
    private val signClickMessageHandler: SignClickMessageHandler,
    private val oneProductMessageHandler: OneProductMessageHandler,
    private val cacheManage: CacheManage,
    private val redPackageMessageHandler: RedPackageMessageHandler,
    private val zyProperties: ZyProperties,
    private val activityMessageHandler: ActivityMessageHandler,
    private val scanService: ScanService,
    private val subscribePOService: SubscribePOService,
    private val baiduService: BaiduService
) {

    fun getSimpleUser(openid: String): User? {
        val userPO = userPOService.getByOpenid(openid) ?: return null
        return userPO.copyPropertiesTo()
    }

    fun getOrCreateAuthUser(wxUser: WxOAuth2UserInfo): User {
        val dbUser = userPOService.getByOpenid(wxUser.openid)
        val newUser = userConvertService.convertToUserPO(wxUser)
        if (dbUser != null) {
            newUser.id = dbUser.id
        }
        userPOService.saveOrUpdate(newUser)
        return newUser.copyPropertiesTo()
    }


    fun queryUsers(nickname: String?, current: Long, size: Long): Page<User> {
        val userPOPage = userPOService.pageUsers(nickname, PageDTO(current, size))
        val userPage: Page<User> = Page.of(current, size, userPOPage.total)
        userPage.records = userPOPage.records.map {
            val user = it.copyPropertiesTo<User>()

            user.subscribes = subscribePOService.getLastSubscribe(it.openid).tolist()
            user.points = userPointsService.getUserPoints(it.openid)
            user
        }
        return userPage
    }

    fun getSimpleUser(id: Long): User? {
        val userPO = userPOService.getById(id) ?: return null
        return userPO.copyPropertiesTo()
    }

    fun getUserDetail(openid: String): User {
        val userPO = userPOService.getByOpenid(openid) ?: throw BizException.buildBizException("用户不存在")
        val user = userPO.copyPropertiesTo<User>()
        user.subscribes = subscribePOService.getSubscribes(userPO.openid)
        user.points = userPointsService.getUserPoints(userPO.openid)
        user.pointsRecords = userPointsService.getPointRecords(userPO.openid)
        user.channels = channelService.getChannels(userPO.openid)
        return user
    }


    @Transactional
    fun userSubscribeEvent(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val userWxInfo: WxMpUser = wxMpService.userService.userInfo(wxMpXmlMessage.fromUser, null)
        val dbUser = userPOService.getByOpenid(userWxInfo.openId)
        val newUser = userConvertService.convertToUserPO(userWxInfo)
        if (dbUser == null) {
            // 不更新，因为关注接口获取不到昵称、头像了
            userPOService.save(newUser)
        }
        val isNewUser = dbUser == null || subscribePOService.getSubscribes(newUser.openid).isEmpty()

        addSubscribeRecord(userWxInfo)
        var replyContent =
            configService.getConfigWithCommon(WxMpConfigStorageHolder.get(), BizConfigEnum.SUBSCRIBE_REPLY_CONTENT) ?: "感谢关注"
        replyContent = replyContent.completeMsgMenu(
            allProductMessageHandler.receivedMessages().first(),
            signClickMessageHandler.receivedMessages().first(),
        )
        wxMpXmlMessage.replyKfMessage(replyContent)

        val qrscene = resolveQrscene(wxMpXmlMessage)

        if (isNewUser)  {
            presentPoints(wxMpXmlMessage)
            handleInviter(qrscene, newUser)
        }

        scanService.handleQrsceneScan(wxMpXmlMessage, qrscene)
        return null
    }

    private fun handleInviter(qrscene: String?, userPO: UserPO) {
        if (qrscene == null) {
            return
        }
        val inviterInfo = decodeInviter(qrscene) ?: return
        if (inviterInfo.openid == userPO.openid) {
            // 排除自己
            return
        }
        userInvitationShipService.addInvite(userPO.openid, inviterInfo)

        redPackageMessageHandler.sendMessage(userPO.openid)

        val findInviteUsers = userInvitationShipService.findInviteUsers(inviterInfo.openid)
        val aim = 3
        if (findInviteUsers.size == aim) {
            redPackageService.sendPackage(inviterInfo.openid)
        } else {
            inviterInfo.openid.replyKfMessage("成功邀请一位用户关注(${findInviteUsers.size}/$aim)")
        }

    }

    private fun resolveQrscene(wxMpXmlMessage: WxMpXmlMessage): String? {
        if (wxMpXmlMessage.event != "subscribe") {
            return null
        }
        return wxMpXmlMessage.eventKey.removePrefix("qrscene_")
    }


    private fun presentPoints(wxMpXmlMessage: WxMpXmlMessage) {
        val configPoints = cacheManage.getBizValue(BizConfigEnum.FIRST_SUBSCRIBE_PRESENT_POINTS)
        val firstSubscribe = """
                        首次关注，赠送小主 ${configPoints.toInt()} 积分~                
                        每天回复${buildMsgMenuUrl("签到", "【签到】")}可以获取积分哦
        
                        只要 $MIN_POINTS_CAN_EXCHANGE_MEMBER 积分就可以兑换会员啦
                        ${buildMsgMenuUrl(ExchangeMemberMessageHandler.receivedMessage, "➜ 戳我兑换会员")}
                    """.trimIndent()
        wxMpXmlMessage.replyKfMessage(firstSubscribe)
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