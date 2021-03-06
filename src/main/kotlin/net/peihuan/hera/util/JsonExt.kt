package net.peihuan.hera.util

import com.google.gson.Gson
import com.google.gson.JsonObject


val gson = Gson()


fun Any?.toJson(): String {
    return gson.toJson(this)
}

fun String?.toJsonObject(): JsonObject {
    return gson.fromJson(this, JsonObject::class.java)
}

inline fun <reified T> String?.toBean(): T {
    return gson.fromJson(this, T::class.java)
}

