package com.kg.gettransfer.cache

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

import com.kg.gettransfer.cache.dao.AccountCachedDao
import com.kg.gettransfer.cache.dao.ConfigsCachedDao
import com.kg.gettransfer.cache.dao.OfferCachedDao
import com.kg.gettransfer.cache.dao.TransferCachedDao
import com.kg.gettransfer.cache.dao.RouteCacheDao
import com.kg.gettransfer.cache.dao.CarrierTripCacheDao
import com.kg.gettransfer.cache.dao.ChatCachedDao
import com.kg.gettransfer.cache.dao.MobileConfigsCachedDao

import com.kg.gettransfer.cache.model.AccountCached
import com.kg.gettransfer.cache.model.ConfigsCached
import com.kg.gettransfer.cache.model.OfferCached
import com.kg.gettransfer.cache.model.TransferCached
import com.kg.gettransfer.cache.model.RouteInfoCached
import com.kg.gettransfer.cache.model.CarrierTripBaseCached
import com.kg.gettransfer.cache.model.CarrierTripMoreCached
import com.kg.gettransfer.cache.model.ChatCached
import com.kg.gettransfer.cache.model.MessageCached
import com.kg.gettransfer.cache.model.NewMessageCached
import com.kg.gettransfer.cache.model.MobileConfigsCached

@Database(entities = arrayOf(
        ConfigsCached::class,
        MobileConfigsCached::class,
        AccountCached::class,
        TransferCached::class,
        OfferCached::class,
        RouteInfoCached::class,
        CarrierTripBaseCached::class,
        CarrierTripMoreCached::class,
        ChatCached::class,
        MessageCached::class,
        NewMessageCached::class), version = 11, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class CacheDatabase: RoomDatabase() {

    abstract fun configsCachedDao(): ConfigsCachedDao
    abstract fun mobileConfigsCachedDao(): MobileConfigsCachedDao
    abstract fun accountCachedDao(): AccountCachedDao
    abstract fun transferCacheDao(): TransferCachedDao
    abstract fun offersCacheDao(): OfferCachedDao
    abstract fun routeCacheDao(): RouteCacheDao
    abstract fun carrierTripCachedDao(): CarrierTripCacheDao
    abstract fun chatCacheDao() : ChatCachedDao
}
