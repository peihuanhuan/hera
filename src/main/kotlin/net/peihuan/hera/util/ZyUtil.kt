package net.peihuan.hera.util

import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder
import net.peihuan.hera.constants.OrderSourceEnum
import java.net.URLDecoder
import java.net.URLEncoder

class ZyUtil {

    companion object {

        fun buildAllProductUrl(openid: String, zyAppid: String): String {
            val channel = buildChannel(openid)
            val url = "https://cdn.wxthe.com/life/#/?appid=$zyAppid&channel=$channel"
            return url
        }


        data class Channel(
            val source: OrderSourceEnum,
            val appid: String,
            val openid: String
        )

        fun buildChannel(openid: String, source: OrderSourceEnum = OrderSourceEnum.BUY): String {
            val channel = Channel(openid = openid, appid = WxMpConfigStorageHolder.get(), source = source)
            return URLEncoder.encode(channel.toJson(), "utf-8")
        }

        fun getChannel(channel: String) : Channel {
            val decode = URLDecoder.decode(channel, "utf-8")
            return decode.toBean()
        }
    }

}