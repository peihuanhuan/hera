package net.peihuan.hera.service.storage

import java.net.URL
import java.util.*


interface StorageService {
    fun upload(objectName: String, filePath: String)

    fun getDownloadUrl(objectName: String): String

    fun getUrl(objectName: String, expire: Date) : URL
}