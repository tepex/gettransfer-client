package com.kg.gettransfer.cache

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

import com.kg.gettransfer.cache.dao.AccountCachedDao
import com.kg.gettransfer.cache.dao.ConfigsCachedDao
import com.kg.gettransfer.cache.dao.OfferCachedDao
import com.kg.gettransfer.cache.dao.TransferCachedDao
import com.kg.gettransfer.cache.dao.RouteCacheDao
import com.kg.gettransfer.cache.model.AccountCached
import com.kg.gettransfer.cache.model.ConfigsCached
import com.kg.gettransfer.cache.model.OfferCached
import com.kg.gettransfer.cache.model.TransferCached
import com.kg.gettransfer.cache.model.RouteInfoCached

@Database(entities = arrayOf(ConfigsCached::class, AccountCached::class, TransferCached::class, OfferCached::class, RouteInfoCached::class), version = 2, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class CacheDatabase: RoomDatabase() {

    abstract fun configsCachedDao(): ConfigsCachedDao
    abstract fun accountCachedDao(): AccountCachedDao
    abstract fun transferCacheDao(): TransferCachedDao
    abstract fun offersCacheDao() : OfferCachedDao
    abstract fun routeCacheDao() : RouteCacheDao
}
