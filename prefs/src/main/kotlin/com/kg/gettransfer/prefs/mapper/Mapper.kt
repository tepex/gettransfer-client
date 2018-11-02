package com.kg.gettransfer.prefs.mapper

interface Mapper<String, T> {
    fun fromJson(json: String): T
    fun toJson(type: T): String
}
