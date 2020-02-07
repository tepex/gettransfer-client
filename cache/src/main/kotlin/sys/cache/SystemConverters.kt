package com.kg.gettransfer.sys.cache

import androidx.room.TypeConverter

import com.kg.gettransfer.cache.model.CurrencyCachedList
import com.kg.gettransfer.cache.model.LocaleCachedList
import com.kg.gettransfer.cache.model.TransportTypesCachedList

import com.kg.gettransfer.core.cache.AddressHistoryList

import kotlinx.serialization.json.JSON

@Suppress("TooManyFunctions")
object SystemConverters {
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
    fun toLocaleCachedList(s: String?): LocaleCachedList? = s?.let { JSON.parse(LocaleCachedList.serializer(), it) }

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
    fun fromBuildsConfigsModelMap(map: BuildsConfigsModelMap?): String? =
        map?.let { JSON.stringify(BuildsConfigsModelMap.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toBuildsConfigsModelMap(s: String?): BuildsConfigsModelMap? =
        s?.let { JSON.parse(BuildsConfigsModelMap.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun fromContactEmailModelList(list: ContactEmailModelList?): String? =
        list?.let { JSON.stringify(ContactEmailModelList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toContactEmailModelList(s: String?): ContactEmailModelList? =
        s?.let { JSON.parse(ContactEmailModelList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun fromAddressHistoryList(list: AddressHistoryList?): String? =
        list?.let { JSON.stringify(AddressHistoryList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toAddressHistoryList(s: String?): AddressHistoryList? =
        s?.let { JSON.parse(AddressHistoryList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun fromFavoriteTransportsList(list: FavoriteTransportsList?): String? =
        list?.let { JSON.stringify(FavoriteTransportsList.serializer(), it) }

    @TypeConverter
    @JvmStatic
    fun toFavoriteTransportsList(s: String?): FavoriteTransportsList? =
        s?.let { JSON.parse(FavoriteTransportsList.serializer(), it) }
}
