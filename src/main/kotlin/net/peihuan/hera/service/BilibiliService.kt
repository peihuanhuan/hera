package net.peihuan.hera.service

import net.peihuan.hera.domain.BilibiliVideo
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.feign.dto.bilibili.BangumiInfo
import net.peihuan.hera.feign.dto.bilibili.Episode
import net.peihuan.hera.feign.dto.bilibili.Quality
import net.peihuan.hera.feign.dto.bilibili.View
import net.peihuan.hera.feign.service.BilibiliFeignService
import net.peihuan.hera.util.getLocationUrl
import org.springframework.stereotype.Service
import java.util.regex.Pattern


@Service
class BilibiliService(private val bilibiliFeignService: BilibiliFeignService) {

    fun resolve2BilibiliVideos(data: String): List<BilibiliVideo> {
        val shortUrls = resolveShortUrls(data)
        val longUrls = shortUrls.mapNotNull { getLocationUrl(it) }
        val videos = mutableListOf<BilibiliVideo>()
        videos.addAll(longUrls.mapNotNull { getBV(it).firstOrNull() })
        videos.addAll(longUrls.mapNotNull { getEp(it).firstOrNull() })

        videos.addAll(getBV(data))
        videos.addAll(getEp(data))
        return videos.distinct()
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

    private fun getBV(data: String): MutableList<BilibiliVideo> {
        val bvids = mutableListOf<BilibiliVideo>()
        val regex =
            Pattern.compile("(http|https):\\/\\/www\\.bilibili\\.com\\/video\\/(BV\\w*)(\\?((.*&p=|p=|)(\\d+)\\S*|\\S*))?\\s*")
                .matcher(data)
        var matchStart = 0
        while (regex.find(matchStart)) {
            bvids.add(BilibiliVideo(regex.group(2), regex.group(6)))
            matchStart = regex.end()
        }
        return bvids
    }

    private fun getEp(data: String): List<BilibiliVideo> {
        val epids = mutableListOf<Int>()
        val regex =
            Pattern.compile("(http|https):\\/\\/www\\.bilibili\\.com\\/bangumi\\/play\\/ep(\\d*)(\\?((.*&p=|p=|)(\\d+)\\S*|\\S*))?\\s*")
                .matcher(data)
        var matchStart = 0
        while (regex.find(matchStart)) {
            epids.add(regex.group(2).toInt())
            matchStart = regex.end()
        }
        return epids.mapNotNull { epid -> getBangumiInfo(epid) }
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

    fun getBangumiInfo(epId: Int): BilibiliVideo? {
        val bangumiIbfo = bilibiliFeignService.getBangumiIbfo(epId)
        val allEpisodes = getAllEpisodes(bangumiIbfo)

        val ep = allEpisodes.first { it.id == epId }
        checkNeedVip(ep)
        return BilibiliVideo(
            aid = ep.aid.toString(),
            bvid = ep.bvid,
            epid = epId,
            title = ep.title,
            cid = ep.cid.toString()
        )
    }

    private fun checkNeedVip(ep: Episode) {
        if (ep.badge.contains("会员")) {
            throw BizException.buildBizException("视频需要大会员，拒绝执行。")
        }
    }

    private fun getAllEpisodes(bangumiIbfo: BilibiliFeignService.Result<BangumiInfo>): MutableList<Episode> {
        val allEpisodes = mutableListOf<Episode>()
        allEpisodes.addAll(bangumiIbfo.result.episodes ?: emptyList())
        bangumiIbfo.result.section?.forEach {
            allEpisodes.addAll(it.episodes)
        }
        return allEpisodes
    }

    fun getAllBangumiInfos(epId: Int): List<BilibiliVideo> {
        val bangumiIbfo = bilibiliFeignService.getBangumiIbfo(epId)
        val allEpisodes = getAllEpisodes(bangumiIbfo)

        return allEpisodes.map { ep ->
            checkNeedVip(ep)
            BilibiliVideo(
                aid = ep.aid.toString(),
                bvid = ep.bvid,
                title = ep.title,
                epid = epId,
                cid = ep.cid.toString()
            )
        }

    }

}