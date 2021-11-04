package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage
import me.chanjar.weixin.mp.bean.result.WxMpUser
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.constants.StatusEnum
import net.peihuan.hera.constants.SubscribeSceneEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.handler.click.ExchangeMemberMessageHandler
import net.peihuan.hera.handler.click.SignClickMessageHandler
import net.peihuan.hera.handler.click.member.AllProductMessageHandler
import net.peihuan.hera.handler.click.member.TencentMessageHandler
import net.peihuan.hera.handler.click.waimai.ElmeWmHandler
import net.peihuan.hera.handler.click.waimai.MeituanWmHandler
import net.peihuan.hera.persistent.po.SubscribePO
import net.peihuan.hera.persistent.po.UserPO
import net.peihuan.hera.persistent.service.SubscribePOService
import net.peihuan.hera.persistent.service.UserPOService
import net.peihuan.hera.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userPOService: UserPOService,
                  private val wxMpService: WxMpService,
                  private val configService: ConfigService,
                  private val userPointsService: UserPointsService,
                  private val cacheManage: CacheManage,
                  private val subscribePOService: SubscribePOService) {

    @Transactional
    fun userSubscribeEvent(wxMpXmlMessage: WxMpXmlMessage): WxMpXmlOutMessage? {
        val userWxInfo: WxMpUser = wxMpService.userService.userInfo(wxMpXmlMessage.fromUser, null)
        val dbUser = userPOService.getByOpenid(userWxInfo.openId)
        val newUser = buildUser(userWxInfo)
        if (dbUser != null) {
            newUser.id = dbUser.id
        }
        userPOService.saveOrUpdate(newUser)
        addSubscribeRecord(userWxInfo)
        var replyContent = configService.getConfigWithCommon(WxMpConfigStorageHolder.get(), BizConfigEnum.SUBSCRIBE_REPLY_CONTENT)
                ?: "感谢关注"
        replyContent = replyContent.completeMsgMenu(MeituanWmHandler.reply,
                ElmeWmHandler.reply,
                AllProductMessageHandler.reply,
                SignClickMessageHandler.reply,
                TencentMessageHandler.reply
        )
        wxMpService.kefuService.sendKefuMessage(buildKfText(wxMpXmlMessage, replyContent))

        if (dbUser == null) {
            firstSubscribe(wxMpXmlMessage)
        }


        return null
    }

    private fun firstSubscribe(wxMpXmlMessage: WxMpXmlMessage) {
        presentPoints(wxMpXmlMessage)
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

    fun buildUser(mpUser: WxMpUser): UserPO {
        // todo 多公众号
        val appid = WxMpConfigStorageHolder.get()
        return UserPO(
                nickname = mpUser.nickname,
                appid = appid,
                openid = mpUser.openId,
                unionid = mpUser.unionId,
                sex = mpUser.sex,
                province = mpUser.province,
                city = mpUser.city,
                country = mpUser.country,
                headimgurl = mpUser.headImgUrl,
                privilege = mpUser.privileges.toJson(),
                remark = mpUser.remark,
                groupid = mpUser.groupId,
                updateTime = null,
        )
        // TODO: 保存头像
        // todo 处理标签
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
}