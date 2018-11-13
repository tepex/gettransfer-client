package com.kg.gettransfer.cache

import android.arch.persistence.room.TypeConverter

import com.kg.gettransfer.cache.model.CurrencyCachedList
import com.kg.gettransfer.cache.model.LocaleCachedList
import com.kg.gettransfer.cache.model.StringList
import com.kg.gettransfer.cache.model.TransportTypesCachedList

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
    fun fromTransportTypesCachedList(list: TransportTypesCachedList?): String? = list?.let { JSON.stringify(TransportTypesCachedList.serializer(), it) }
    
    @TypeConverter
    @JvmStatic
    fun toTransportTypesCachedList(s: String?): TransportTypesCachedList? = s?.let { JSON.parse(TransportTypesCachedList.serializer(), it) }
        
    @TypeConverter
    @JvmStatic
    fun fromLocaleCachedList(list: LocaleCachedList?): String? = list?.let { JSON.stringify(LocaleCachedList.serializer(), it) }
    
    @TypeConverter
    @JvmStatic
    fun toLocaleCachedList(s: String?): LocaleCachedList? = s?.let { JSON.parse(LocaleCachedList.serializer(), it) }
        
    @TypeConverter
    @JvmStatic
    fun fromCurrencyCachedList(list: CurrencyCachedList?): String? = list?.let { JSON.stringify(CurrencyCachedList.serializer(), it) }
    
    @TypeConverter
    @JvmStatic
    fun toCurrencyCachedList(s: String?): CurrencyCachedList? = s?.let { JSON.parse(CurrencyCachedList.serializer(), it) }
}
