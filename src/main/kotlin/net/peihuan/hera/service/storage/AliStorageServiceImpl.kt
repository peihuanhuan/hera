package net.peihuan.hera.service.storage

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.GeneratePresignedUrlRequest
import com.aliyun.oss.model.ObjectMetadata
import com.aliyun.oss.model.PutObjectRequest
import mu.KotlinLogging
import net.peihuan.hera.config.AliyunOssProperties
import org.springframework.stereotype.Service
import java.io.FileInputStream
import java.net.URL
import java.net.URLEncoder
import java.util.*
import javax.annotation.PostConstruct


@Service
class AliStorageServiceImpl(private val aliyunOssProperties: AliyunOssProperties) : StorageService {

    private val log = KotlinLogging.logger {}

    private lateinit var ossClient: OSS

    @PostConstruct
    fun init() {
        ossClient = OSSClientBuilder().build(
            aliyunOssProperties.endpoint,
            aliyunOssProperties.accessKeyId,
            aliyunOssProperties.accessKeySecret
        )
    }

    override fun upload(objectName: String, filePath: String) {
        val objectMetadata = ObjectMetadata()
        objectMetadata.contentDisposition = "attachement"
        val putObjectRequest = PutObjectRequest(aliyunOssProperties.bucketName, objectName, FileInputStream(filePath), objectMetadata)
        ossClient.putObject(putObjectRequest)
    }

    override fun getDownloadUrl(objectName: String): String {
        // return "https://${aliyunOssProperties.bucketName}.${aliyunOssProperties.endpoint}/${URLEncoder.encode(objectName, "utf-8")}"
        return "${aliyunOssProperties.cdnHost}/${URLEncoder.encode(objectName, "utf-8")}"
    }

    override fun getUrl(objectName: String, expire: Date): URL {
        val request = GeneratePresignedUrlRequest(aliyunOssProperties.bucketName, objectName)
        request.expiration = expire
        return ossClient.generatePresignedUrl(request)
    }

}