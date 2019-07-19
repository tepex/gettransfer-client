@file:Suppress("TooManyFunctions")
package com.kg.gettransfer.cache

import android.arch.persistence.room.TypeConverter

import com.kg.gettransfer.cache.model.CurrencyCachedList
import com.kg.gettransfer.cache.model.LocaleCachedList
import com.kg.gettransfer.cache.model.StringList
import com.kg.gettransfer.cache.model.TransportTypesCachedList
import com.kg.gettransfer.cache.model.IntList
import com.kg.gettransfer.cache.model.BookNowOfferCachedMap
import com.kg.gettransfer.cache.model.ChatAccountsCachedMap

import kotlinx.serialization.json.JSON

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
    fun fromTransportTypesCachedList(list: TransportTypesCachedList?): String? =
        list?.let { JSON.stringify(TransportTypesCachedList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toTransportTypesCachedList(s: String?): TransportTypesCachedList? =
        s?.let { JSON.parse(TransportTypesCachedList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun fromLocaleCachedList(list: LocaleCachedList?): String? =
        list?.let { JSON.stringify(LocaleCachedList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toLocaleCachedList(s: String?): LocaleCachedList? =
        s?.let { JSON.parse(LocaleCachedList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun fromCurrencyCachedList(list: CurrencyCachedList?): String? =
        list?.let { JSON.stringify(CurrencyCachedList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toCurrencyCachedList(s: String?): CurrencyCachedList? =
        s?.let { JSON.parse(CurrencyCachedList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun fromChatAccountCachedMap(map: ChatAccountsCachedMap?): String? =
        map?.let { JSON.stringify(ChatAccountsCachedMap.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toChatAccountCachedMap(s: String?): ChatAccountsCachedMap? =
        s?.let { JSON.parse(ChatAccountsCachedMap.serializer(), it) }
}
