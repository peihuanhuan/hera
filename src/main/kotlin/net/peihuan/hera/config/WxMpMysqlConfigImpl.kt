package net.peihuan.hera.config

import me.chanjar.weixin.common.enums.TicketType
import me.chanjar.weixin.common.redis.WxRedisOps
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl
import java.util.concurrent.TimeUnit

class WxMpMysqlConfigImpl(val mysqlOps: WxRedisOps, val keyPrefix: String) : WxMpDefaultConfigImpl() {

    private val ACCESS_TOKEN_KEY_TPL = "%s:access_token:%s"
    private val TICKET_KEY_TPL = "%s:ticket:key:%s:%s"
    private val LOCK_KEY_TPL = "%s:lock:%s:"


    private lateinit var accessTokenKey: String
    private lateinit var lockKey: String

    /**
     * 每个公众号生成独有的存储key.
     */
    override fun setAppId(appId: String) {
        super.setAppId(appId)
        this.accessTokenKey = String.format(ACCESS_TOKEN_KEY_TPL, this.keyPrefix, appId)
        this.lockKey = String.format(LOCK_KEY_TPL, this.keyPrefix, appId)
        accessTokenLock = this.mysqlOps.getLock(lockKey + "accessTokenLock")
        jsapiTicketLock = this.mysqlOps.getLock(lockKey + "jsapiTicketLock")
        sdkTicketLock = this.mysqlOps.getLock(lockKey + "sdkTicketLock")
        cardApiTicketLock = this.mysqlOps.getLock(lockKey + "cardApiTicketLock")
    }

    private fun getTicketRedisKey(type: TicketType): String? {
        return String.format(TICKET_KEY_TPL, this.keyPrefix, appId, type.code)
    }

    override fun getAccessToken(): String? {
        return mysqlOps.getValue(this.accessTokenKey)
    }

    override fun isAccessTokenExpired(): Boolean {
        val expire = mysqlOps.getExpire(this.accessTokenKey)
        return expire == null || expire < 2
    }

    @Synchronized
    override fun updateAccessToken(accessToken: String?, expiresInSeconds: Int) {
        mysqlOps.setValue(this.accessTokenKey, accessToken, expiresInSeconds - 200, TimeUnit.SECONDS)
    }

    override fun expireAccessToken() {
        mysqlOps.expire(this.accessTokenKey, 0, TimeUnit.SECONDS)
    }

    override fun getTicket(type: TicketType): String? {
        return mysqlOps.getValue(getTicketRedisKey(type))
    }

    override fun isTicketExpired(type: TicketType): Boolean {
        return mysqlOps.getExpire(getTicketRedisKey(type)) < 2
    }

    @Synchronized
    override fun updateTicket(type: TicketType, jsapiTicket: String?, expiresInSeconds: Int) {
        mysqlOps.setValue(getTicketRedisKey(type), jsapiTicket, expiresInSeconds - 200, TimeUnit.SECONDS)
    }

    override fun expireTicket(type: TicketType) {
        mysqlOps.expire(getTicketRedisKey(type), 0, TimeUnit.SECONDS)
    }

}