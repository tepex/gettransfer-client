package com.kg.gettransfer.cache

import android.arch.persistence.room.TypeConverter

import kotlinx.serialization.json.JSON

class StringListConverter {
    @TypeConverter
    fun fromList(list: List<String>): String = JSON.stringify(list)
    @TypeConverter
    fun toList(data: String): List<String> = JSON.parse(data)
}
