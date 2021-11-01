package net.peihuan.hera.util

import com.google.gson.Gson
import com.google.gson.JsonObject


private val gson = Gson()


fun Any?.toJson(): String {
    return gson.toJson(this)
}

fun String?.toJsonObject(): JsonObject {
    return gson.fromJson(this, JsonObject::class.java)
}

