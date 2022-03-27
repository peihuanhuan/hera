package net.peihuan.hera.util

import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.http.Header
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.protocol.HTTP
import java.io.File
import java.io.IOException
import kotlin.system.measureTimeMillis

private val log = KotlinLogging.logger {}


private val config =
    RequestConfig.custom()
        .setConnectTimeout(5000)
        .setConnectionRequestTimeout(1000)
        .setSocketTimeout(5000)
        .setRedirectsEnabled(false)
        .build() //不允许重定向
private val locationHttpClient = HttpClients.custom().setDefaultRequestConfig(config).build()


fun getLocationUrl(url: String): String? {
    var location: String? = null
    var response: CloseableHttpResponse? = null
    try {
        response = locationHttpClient.execute(HttpGet(url))
        val responseCode = response.statusLine.statusCode
        if (responseCode == 302) {
            val locationHeader: Header = response.getFirstHeader("Location")
            location = locationHeader.getValue()
        }

    } catch (e: Exception) {
        log.error(e.message, e)
    } finally {
        response?.close()
    }
    return location
}



val downloadHttpClient = HttpClients.custom().setDefaultRequestConfig(config).build()

fun doDownloadBilibiliVideo(url: String, descFile: File, bvid: String, retryTime: Int = 3) {
    val headers = mapOf(
        "Accept" to "*/*",
        "Accept-Encoding" to "gzip, deflate, br",
        "Accept-Language" to "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7",
        "Connection" to "keep-alive",
        "origin" to "https://www.bilibili.com",
        "referer" to "https://www.bilibili.com/video/$bvid",
        "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
    )
    var retry = 0;
    log.info { "====== 开始下载 ${descFile.absolutePath}" }
    while (retry++ < retryTime && !descFile.exists()) {
        val measureTimeMillis = measureTimeMillis { doDownload(url, descFile, headers) }
        log.info("下载完成，耗时 {} 秒", measureTimeMillis / 1000)
    }
    if (!descFile.exists()) {
        log.error { "下载失败，没有文件 $bvid $url  " }
    }
}

fun doDownload(url: String, descFile: File, header: Map<String, String>, retryTime: Int) {
    var retry = 0;
    while (retry++ < retryTime && !descFile.exists()) {
        doDownload(url, descFile, header)
    }
}
fun doDownload(url: String, descFile: File, header: Map<String, String>) {

    val post = HttpGet(url)
    post.addHeader(HTTP.CONTENT_ENCODING, "UTF-8")

    var response: CloseableHttpResponse? = null
    try {
        header.forEach { (t, u) ->
            post.addHeader(t, u)
        }
        response = downloadHttpClient.execute(post)
        if (response.statusLine.statusCode == 200) {
            FileUtils.copyToFile(response.entity.content, descFile)
        } else {
            println(response.statusLine)
        }
    } catch (e: Exception) {
        //fixme org.apache.http.ConnectionClosedException: Premature end of Content-Length delimited message body (expected: 10,360,240; received: 122,064)
        log.error(e.message, e)
        FileUtils.deleteQuietly(descFile)
    } finally {
        if (response != null) {
            try {
                response.close()
            } catch (e: IOException) {
                log.error { e }
            }
        }
    }
}

fun upload(url: String ,byteArray: ByteArray) {
    val put = HttpPut(url)
    put.entity = ByteArrayEntity(byteArray)
    put.addHeader(HTTP.CONTENT_ENCODING, "UTF-8")
    var response: CloseableHttpResponse? = null
    try {
        log.info { "开始上传 $url" }
        response = downloadHttpClient.execute(put)
        if (response.statusLine.statusCode == 200) {
            log.info { "上传成功 $url" }
        } else {
            log.error { response.statusLine }
        }
    } catch (e: Exception) {
        log.error { e }
    } finally {
        if (response != null) {
            try {
                response.close()
            } catch (e: IOException) {
                log.error { e }
            }
        }
    }
}