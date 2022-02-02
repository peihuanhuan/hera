package net.peihuan.hera.util

import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.http.Header
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.protocol.HTTP
import java.io.File
import java.io.IOException

private val log = KotlinLogging.logger {}


private val config =
    RequestConfig.custom()
        .setConnectTimeout(50000)
        .setConnectionRequestTimeout(10000)
        .setSocketTimeout(50000)
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



val downloadHttpClient = HttpClientBuilder.create().build()

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