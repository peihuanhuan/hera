package net.peihuan.hera.service

import net.peihuan.hera.domain.BilibiliVideo
import net.peihuan.hera.feign.dto.bilibili.Quality
import net.peihuan.hera.feign.dto.bilibili.View
import net.peihuan.hera.feign.service.BilibiliFeignService
import net.peihuan.hera.util.getLocationUrl
import org.springframework.stereotype.Service
import java.util.regex.Pattern


@Service
class BilibiliService(private val bilibiliFeignService: BilibiliFeignService) {

    fun resolve2BilibiliVideos(data: String) : List<BilibiliVideo> {
        val shortUrls = resolveShortUrls(data)
        val longUrls = shortUrls.mapNotNull { getLocationUrl(it) }
        val shortUrlBvids = longUrls.map { getBVReg(it)[0] }

        val bvids = getBVReg(data)
        bvids.addAll(shortUrlBvids)
        return bvids.distinct()
    }


    private fun resolveShortUrls(data: String): List<String> {
        val shortUrls = mutableListOf<String>()
        val regex = Pattern.compile("((http|https):\\/\\/b23\\.tv\\/\\w*)(\\?.*)? ?.*?").matcher(data)
        var matchStart = 0
        while (regex.find(matchStart)) {
            shortUrls.add(regex.group(1))
            matchStart = regex.end()
        }
        return shortUrls
    }

    private fun getBVReg(data: String): MutableList<BilibiliVideo> {
        val bvids = mutableListOf<BilibiliVideo>()
        val regex = Pattern.compile("(http|https):\\/\\/www\\.bilibili\\.com\\/video\\/(BV\\w*)(\\?((.*&p=|p=|)(\\d+)\\S*|\\S*))?\\s*").matcher(data)
        var matchStart = 0
        while (regex.find(matchStart)) {
            bvids.add(BilibiliVideo(regex.group(2), regex.group(6)))
            matchStart = regex.end()
        }
        return bvids
    }


    fun getViewByAid(aid: String): View {
        return bilibiliFeignService.getView(aid = aid).data
    }

    fun getViewByBvid(bvid: String): View {
        return bilibiliFeignService.getView(bvid = bvid).data
    }

    fun getDashAudioPlayUrl(avid: String, cid: String): String? {
        val dashPlayurl =
            bilibiliFeignService.dashPlayurl(avid = avid, cid = cid, Quality.P_360.code)
        val audios = dashPlayurl.data.dash?.audio
        if (audios.isNullOrEmpty()) {
            return null
        }
        return audios.maxByOrNull { it.bandwidth }!!.base_url
    }

    fun getFlvPlayUrl(avid: String, cid: String): String? {
        val dashPlayurl =
            bilibiliFeignService.flvPlayurl(avid = avid, cid = cid, Quality.P_360.code)
        return dashPlayurl.data.durl?.first()?.url
    }

}