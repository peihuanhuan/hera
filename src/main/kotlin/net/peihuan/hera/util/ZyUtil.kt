package net.peihuan.hera.util

class ZyUtil {

    companion object {
        private const val splitChar = "_division_"
        fun getChannelOpenid(channel: String) = channel.split(splitChar)[1]

        fun buildAllProductUrl(channelId: Long, zyAppid: String): String {
            val url = "https://cdn.wxthe.com/life/#/?appid=$zyAppid&channel=$channelId"
            return url
        }

        fun buildOneProductUrl(pcode: String, channelId: Long, zyAppid: String): String {
            return "https://cdn.wxthe.com/life/#/pages/card/recharge?pcode=${pcode}&appid=${zyAppid}&channel=$channelId"
        }
    }

}