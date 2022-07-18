package net.peihuan.hera.service

import net.peihuan.hera.domain.BilibiliVideo
import net.peihuan.hera.exception.BizException
import net.peihuan.hera.feign.dto.bilibili.*
import net.peihuan.hera.feign.service.ApiBilibiliFeignService
import net.peihuan.hera.feign.service.WwwBilibiliFeignService
import net.peihuan.hera.util.getLocationUrl
import net.peihuan.hera.util.getUrlParams
import org.springframework.stereotype.Service
import java.util.regex.Pattern


@Service
class BilibiliService(
    private val apiBilibiliFeignService: ApiBilibiliFeignService,
    private val wwwBilibiliFeignService: WwwBilibiliFeignService,
    ) {

    fun getMusicUrl(sid: String) : List<String> {
        val musicUrlResp = wwwBilibiliFeignService.getMusicUrl(sid = sid, quality = "2", privilege = "2")
        return musicUrlResp.data.cdns
    }


    fun resolve2BilibiliVideos(data: String): List<BilibiliVideo> {
        val shortUrls = resolveShortUrls(data)
        val longUrls = shortUrls.mapNotNull { getLocationUrl(it) }

        // 将原始长链接追加到原本数据中进行解析
        var newData = data
        longUrls.forEach {
            newData += "\n $it "
        }

        val videos = mutableListOf<BilibiliVideo>()
        videos.addAll(resolveBvSimpleInfo(newData))
        videos.addAll(getEpCompleteInfo(newData))
        videos.addAll(resolveBvSimpleInfoFromPram(newData))
        videos.addAll(resolveMusicFullInfo(newData))

        val needRemove  = mutableListOf<BilibiliVideo>()
        videos.forEach { bilibiliVideo ->
            if (bilibiliVideo.epid != null || bilibiliVideo.sid != null) {
                // 音频、ep 已经填充过属性
                return@forEach
            }
            val view = getViewByBvid(bilibiliVideo.bvid)
            if (view != null) {
                assembleBilibiliVideo(bilibiliVideo, view)
            } else {
                needRemove.add(bilibiliVideo)
            }
        }
        videos.removeAll(needRemove)
        return videos.distinct()
    }

    fun findAllBilibiliVideos(bilibiliVideo: BilibiliVideo): List<BilibiliVideo> {
        if (bilibiliVideo.epid != null) {
            return getAllBangumiInfos(bilibiliVideo.epid)
        }
        val view = getViewByBvid(bilibiliVideo.bvid)
        if (view == null) {
            return emptyList()
        }
        return view.pages.map { page -> convert2BilibiliVideo(view, page) }
    }

    private fun assembleBilibiliVideo(bilibiliVideo: BilibiliVideo, view: View) {
        bilibiliVideo.aid = view.aid
        bilibiliVideo.bvid = view.bvid
        bilibiliVideo.mid = view.owner.mid
        bilibiliVideo.title = view.title
        if (bilibiliVideo.page == null) {
            bilibiliVideo.duration = view.duration
            bilibiliVideo.cid = view.cid
        } else {
            val pageVideo = view.pages.first { page -> page.page.toString() == bilibiliVideo.page }
            bilibiliVideo.duration = pageVideo.duration
            bilibiliVideo.cid = pageVideo.cid
            bilibiliVideo.partTitle = pageVideo.part
        }
    }

    private fun convert2BilibiliVideo(view: View, page: VideoPage): BilibiliVideo {

        val bilibiliVideo = BilibiliVideo(bvid = view.bvid)

        bilibiliVideo.aid = view.aid
        bilibiliVideo.bvid = view.bvid
        bilibiliVideo.mid = view.owner.mid
        bilibiliVideo.duration = page.duration
        // bilibiliVideo.page = page.page.toString()
        // bilibiliVideo.partTitle = page.part
        bilibiliVideo.cid = page.cid
        bilibiliVideo.title = "P${page.page} ${page.part}"
        return bilibiliVideo
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

    private fun resolveBvSimpleInfo(data: String): MutableList<BilibiliVideo> {
        val bvids = mutableListOf<BilibiliVideo>()
        val regex =
            Pattern.compile("\\S*bilibili\\.com\\/video\\/(BV\\w*)(\\?((.*&p=|p=|)(\\d+)\\S*|\\S*))?\\s*")
                .matcher(data)
        var matchStart = 0
        while (regex.find(matchStart)) {
            bvids.add(BilibiliVideo(bvid = regex.group(1), page = regex.group(5)))
            matchStart = regex.end()
        }
        return bvids
    }

    //  https://www.bilibili.com/audio/au2831765?xxx=1
    private fun resolveMusicFullInfo(data: String): MutableList<BilibiliVideo> {
        val videos = mutableListOf<BilibiliVideo>()
        val regex =
            Pattern.compile("\\S*\\.bilibili\\.com\\/audio\\/au(\\d*)(\\?((.*&p=|p=|)(\\d+)\\S*|\\S*))?\\s*")
                .matcher(data)
        var matchStart = 0
        while (regex.find(matchStart)) {
            val sid = regex.group(1)
            val musicInfoResp = wwwBilibiliFeignService.getMusicInfo(sid)

            val musicInfo = musicInfoResp.data
            val bv = BilibiliVideo(
                aid = musicInfo.aid.toString(),
                bvid = musicInfo.bvid?:"",
                sid = sid,
                cid = musicInfo.cid.toString(),
                title = musicInfo.title,
                duration = musicInfo.duration)
            videos.add(bv)
            matchStart = regex.end()
        }
        return videos
    }

    private fun getEpCompleteInfo(data: String): List<BilibiliVideo> {
        val epids = mutableListOf<Int>()
        val regex =
            Pattern.compile("\\S*bilibili\\.com\\/bangumi\\/play\\/ep(\\d*)(\\?((.*&p=|p=|)(\\d+)\\S*|\\S*))?\\s*")
                .matcher(data)
        var matchStart = 0
        while (regex.find(matchStart)) {
            epids.add(regex.group(1).toInt())
            matchStart = regex.end()
        }
        return epids.mapNotNull { epid -> getBangumiInfo(epid) }
    }

    /**
     *  https://www.bilibili.com/festival/2021bnj?bvid=BV1Do4y1d7K7   支持这种类型的视频
     */
    fun resolveBvSimpleInfoFromPram(data: String): MutableList<BilibiliVideo> {
        val bvids = mutableListOf<BilibiliVideo>()
        val regex =
            Pattern.compile("www\\.bilibili\\.com\\S*\\s*")
                .matcher(data)
        var matchStart = 0
        while (regex.find(matchStart)) {
            val wholeUrl = regex.group(0)
            val bvid = getUrlParams(wholeUrl)["bvid"]
            if (bvid != null) {
                bvids.add(BilibiliVideo(bvid = bvid))
            }
            matchStart = regex.end()
        }
        return bvids
    }


    fun getViewByAid(aid: String): View? {
        return apiBilibiliFeignService.getView(aid = aid).data
    }

    fun getViewByBvid(bvid: String): View? {
        return apiBilibiliFeignService.getView(bvid = bvid).data
    }

    fun getDashAudioPlayUrl(avid: String, cid: String): List<String> {
        val dashPlayurl =
            apiBilibiliFeignService.dashPlayurl(avid = avid, cid = cid, Quality.P_360.code)
        var audios = dashPlayurl.data!!.dash?.audio
        if (audios.isNullOrEmpty()) {
            return emptyList()
        }
        val allUrls = mutableListOf<String>()
        audios = audios.sortedByDescending { it.bandwidth }
        audios.forEach {
            allUrls.add(it.baseUrl)
            allUrls.addAll(it.backupUrl ?: emptyList())
        }
        return allUrls
    }

    fun getFlvPlayUrl(avid: String, cid: String, quality: Quality): List<String> {
        val response = apiBilibiliFeignService.flvPlayurl(avid = avid, cid = cid, quality = quality.code)
        if (response.data!!.durl == null) {
            return emptyList()
        }
        val urls = mutableListOf<String>()
        response.data?.durl?.forEach {
            urls.add(it.url)
            urls.addAll(it.backup_url ?: emptyList())
        }
        return urls
    }

    fun getBangumiInfo(epId: Int): BilibiliVideo? {
        val bangumiIbfo: ApiBilibiliFeignService.Result<BangumiInfo> = apiBilibiliFeignService.getBangumiIbfo(epId)
        val allEpisodes = getAllEpisodes(bangumiIbfo)

        val ep: Episode = allEpisodes.first { it.id == epId }
        checkNeedVip(ep)
        return BilibiliVideo(
            aid = ep.aid.toString(),
            bvid = ep.bvid!!,
            epid = epId,
            title = ep.share_copy,
            mid = "",
            // ep 时长单位为毫秒
            duration = ep.duration?.div(1000),
            cid = ep.cid.toString(),
        )
    }

    private fun checkNeedVip(ep: Episode) {
        if ((ep.badge ?: "").contains("会员")) {
            throw BizException.buildBizException("视频需要大会员，拒绝执行。")
        }
    }

    private fun getAllEpisodes(bangumiIbfo: ApiBilibiliFeignService.Result<BangumiInfo>): MutableList<Episode> {
        val allEpisodes = mutableListOf<Episode>()
        allEpisodes.addAll(bangumiIbfo.result.episodes ?: emptyList())
        bangumiIbfo.result.section?.forEach {
            allEpisodes.addAll(it.episodes)
        }
        return allEpisodes
    }

    fun getAllBangumiInfos(epId: Int): List<BilibiliVideo> {
        val bangumiIbfo = apiBilibiliFeignService.getBangumiIbfo(epId)
        val allEpisodes = getAllEpisodes(bangumiIbfo)

        return allEpisodes.map { ep ->
            checkNeedVip(ep)
            BilibiliVideo(
                aid = ep.aid.toString(),
                bvid = ep.bvid!!,
                title = ep.title,
                epid = epId,
                cid = ep.cid.toString()
            )
        }

    }

}