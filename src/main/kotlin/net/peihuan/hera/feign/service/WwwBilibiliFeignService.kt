package net.peihuan.hera.feign.service

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import net.peihuan.hera.feign.dto.bilibili.MusicInfo
import net.peihuan.hera.feign.dto.bilibili.MusicUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam

// @FeignClient(
//     name = "wwwbilibili",
//     url = "https://www.bilibili.com",
// )
@Service
class WwwBilibiliFeignService(val okHttpClient: OkHttpClient, val gson: Gson) {


    data class Response<T>(
        val code: Int,
        val msg: String,
        val data: T
    )

    // @GetMapping("audio/music-service-c/web/url")
    fun getMusicUrl(
        @RequestParam("sid") sid: String,
        @RequestParam("privilege") privilege: String,
        @RequestParam("quality") quality: String
    ): Response<MusicUrl> {
        val url = "https://www.bilibili.com/audio/music-service-c/web/url".toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("sid", sid)
            .addQueryParameter("privilege", privilege)
            .addQueryParameter("quality", quality)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()

        val json = response.body?.string()
        val type = object: TypeToken<Response<MusicUrl>>(){}.type
        return gson.fromJson(json, type)
    }

    // @GetMapping("audio/music-service-c/web/song/info")
    fun getMusicInfo(@RequestParam("sid") sid: String): Response<MusicInfo> {
        val url = "https://www.bilibili.com/audio/music-service-c/web/song/info".toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("sid", sid)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = okHttpClient.newCall(request).execute()

        val json = response.body?.string()
        val type = object: TypeToken<Response<MusicInfo>>(){}.type
        return gson.fromJson(json, type)
    }

}