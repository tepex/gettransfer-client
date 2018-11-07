package com.kg.gettransfer.cache

import android.arch.persistence.room.TypeConverter

class StringListConverter {

    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun toList(data: String) : List<String> = data.split(",").map { it.trim() }
}