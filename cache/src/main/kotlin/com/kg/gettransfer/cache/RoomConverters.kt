package com.kg.gettransfer.cache

import android.arch.persistence.room.TypeConverter

import com.kg.gettransfer.cache.model.StringList

import kotlinx.serialization.json.JSON

object RoomConverters {
    @TypeConverter
    @JvmStatic
    fun fromList(list: StringList?): String? = list?.let { JSON.stringify(StringList.serializer(), it) }
    
    @TypeConverter
    @JvmStatic
    fun toList(s: String?): StringList? = s?.let { JSON.parse(StringList.serializer(), it) }    
}
