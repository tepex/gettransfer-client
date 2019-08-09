package com.kg.gettransfer.cache

import androidx.room.TypeConverter

import com.kg.gettransfer.cache.model.BookNowOfferCachedMap
import com.kg.gettransfer.cache.model.ChatAccountsCachedMap
import com.kg.gettransfer.cache.model.IntList
import com.kg.gettransfer.cache.model.StringList

import kotlinx.serialization.json.JSON

@Suppress("TooManyFunctions")
object RoomConverters {
    @TypeConverter
    @JvmStatic
    fun fromStringList(list: StringList?): String? = list?.let { JSON.stringify(StringList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toStringList(s: String?): StringList? = s?.let { JSON.parse(StringList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun fromIntList(list: IntList?): String? = list?.let { JSON.stringify(IntList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toIntList(s: String?): IntList? = s?.let { JSON.parse(IntList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun fromBookNowOfferCachedMap(map: BookNowOfferCachedMap?): String? =
        map?.let { JSON.stringify(BookNowOfferCachedMap.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toBookNowOfferCachedMap(s: String?): BookNowOfferCachedMap? =
        s?.let { JSON.parse(BookNowOfferCachedMap.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun fromChatAccountCachedMap(map: ChatAccountsCachedMap?): String? =
        map?.let { JSON.stringify(ChatAccountsCachedMap.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toChatAccountCachedMap(s: String?): ChatAccountsCachedMap? =
        s?.let { JSON.parse(ChatAccountsCachedMap.serializer(), it) }
}
