package com.kg.gettransfer.sys.cache

import android.arch.persistence.room.TypeConverter

import kotlinx.serialization.json.JSON

object SystemConverters {
    @TypeConverter
    @JvmStatic
    fun fromBuildsConfigsCachedMap(map: BuildsConfigsCachedMap?): String? =
        map?.let { JSON.stringify(BuildsConfigsCachedMap.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toBuildsConfigsCachedMap(s: String?): BuildsConfigsCachedMap? =
        s?.let { JSON.parse(BuildsConfigsCachedMap.serializer(), it) }
}
