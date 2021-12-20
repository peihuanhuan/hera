package net.peihuan.hera.service

import me.chanjar.weixin.mp.api.WxMpService
import mu.KotlinLogging
import net.peihuan.hera.config.HeraProperties
import net.peihuan.hera.constants.BizConfigEnum
import net.peihuan.hera.domain.CacheManage
import net.peihuan.hera.handler.click.ExchangeMemberMessageHandler
import net.peihuan.hera.persistent.po.SignPO
import net.peihuan.hera.persistent.service.SignPOService
import net.peihuan.hera.util.MIN_POINTS_CAN_EXCHANGE_MEMBER
import net.peihuan.hera.util.buildMsgMenuUrl
import net.peihuan.hera.util.randomGaussianPoints
import org.joda.time.DateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignService(private val signPOService: SignPOService,
                  private val wxMpService: WxMpService,
                  private val heraProperties: HeraProperties,
                  private val cacheManage: CacheManage,
                  private val userPointsService: UserPointsService) {

    private val logger = KotlinLogging.logger {}


    // 正态分布随机积分


    @Transactional
    fun sign(openid: String): String {
        val lastSign = signPOService.getLastSign(openid)
        if(lastSign != null && DateTime.now().withTimeAtStartOfDay().isBefore(lastSign.createTime!!.time)) {
            return "今天已经签到过啦"
        }

        val expect = cacheManage.getBizValue(BizConfigEnum.SIGN_PRESENT_POINTS_EXPECT)
        val variance = cacheManage.getBizValue(BizConfigEnum.SIGN_PRESENT_POINTS_VARIANCE)
        val points = randomGaussianPoints(expect.toInt(), variance.toInt())
        val po = SignPO(openid = openid, points = points)
        signPOService.save(po)
        val userPoints = userPointsService.addUserPoints(openid, points, "每日签到")
        return """
            今天签到获得了 $points 积分
            
            小主已经有 ${userPoints.points} 积分
            
            只要 $MIN_POINTS_CAN_EXCHANGE_MEMBER 积分就可以兑换会员了，快试试吧！
            
            ——————————————
            ${buildMsgMenuUrl(ExchangeMemberMessageHandler.receivedMessage, "➜ 戳我进行兑换会员")}
            
            明天不要忘记签到哦~"       
        """.trimIndent()
    }


    @Transactional
    fun funnySign(openid: String): String {


        val points = 1000
        val po = SignPO(openid = openid, points = points)
        signPOService.save(po)
        val userPoints = userPointsService.addUserPoints(openid, points, "每日签到")
        return """
            哇 你好厉害，竟然获得了 $points 积分！！！！
            
            亲亲已经有 ${userPoints.points} 积分了
            
            只要 $MIN_POINTS_CAN_EXCHANGE_MEMBER 积分就可以兑换会员了，快试试吧！
            
            ——————————————
            ${buildMsgMenuUrl(ExchangeMemberMessageHandler.receivedMessage, "➜ 戳我进行兑换会员")}
            
            明天别签到了，穷了。"       
        """.trimIndent()
    }
}