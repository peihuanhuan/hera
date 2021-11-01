package net.peihuan.hera.util

import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder

class ZyUtil {

    companion object {

        fun buildAllProductUrl(openid: String, zyAppid: String): String {
            val channel = buildChannel(openid)
            val url = "https://cdn.wxthe.com/life/#/?appid=$zyAppid&channel=$channel"
            return url
        }


        private const val splitChar = "_division_"
        fun buildChannel(openid: String): String {
            val appid = WxMpConfigStorageHolder.get()
            return "fwh_$appid$splitChar$openid"
        }

        fun getChannelOpenid(channel: String) = channel.split(splitChar)[1]
    }

}