package net.peihuan.hera.feign.service

import net.peihuan.hera.feign.dto.bilibili.BangumiInfo
import net.peihuan.hera.feign.dto.bilibili.DashPlayUrl
import net.peihuan.hera.feign.dto.bilibili.View
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "bilibili",
    url = "https://api.bilibili.com",
)
interface BilibiliFeignService {


    data class Response<T>(
        val code: Int,
        val message: String,
        val data: T
    )

    data class Result<T>(
        val code: Int,
        val message: String,
        val result: T
    )

    @GetMapping("x/player/playurl")
    fun dashPlayurl(
        @RequestParam("avid") avid: String,
        @RequestParam("cid") cid: String,
        @RequestParam("qn") quality: Int,
        @RequestParam("otype") otype: String = "json",
        @RequestParam("fourk") fourk: String = "1",
        @RequestParam("fnver") fnver: String = "0",
        @RequestParam("fnval") fnval: String = "80",
    ): Response<DashPlayUrl>


    @GetMapping("x/player/playurl")
    fun flvPlayurl(
        @RequestParam("avid") avid: String,
        @RequestParam("cid") cid: String,
        @RequestParam("qn") quality: Int,
        @RequestParam("otype") otype: String = "json",
        @RequestParam("fourk") fourk: String = "1",
        @RequestParam("fnver") fnver: String = "0",
        @RequestParam("fnval") fnval: String = "0",
    ): Response<DashPlayUrl>

    @GetMapping("x/web-interface/view")
    fun getView(
        @RequestParam("bvid") bvid: String? = null,
        @RequestParam("aid") aid: String? = null
    ): Response<View>

    @GetMapping("pgc/view/web/season")
    fun getBangumiIbfo(@RequestParam("ep_id") epId: Int): Result<BangumiInfo>

}