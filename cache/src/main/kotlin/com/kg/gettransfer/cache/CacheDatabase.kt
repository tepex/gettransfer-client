package com.kg.gettransfer.cache

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

import com.kg.gettransfer.cache.dao.AccountCachedDao
import com.kg.gettransfer.cache.dao.ConfigsCachedDao
import com.kg.gettransfer.cache.dao.OfferCachedDao
import com.kg.gettransfer.cache.dao.TransferCachedDao

import com.kg.gettransfer.cache.model.ConfigsCached
import com.kg.gettransfer.cache.model.AccountCached
//import com.kg.gettransfer.cache.model.OfferCached
//import com.kg.gettransfer.cache.model.TransferCached

@Database(entities = arrayOf(/*OfferCached::class, TransferCached::class,*/ConfigsCached::class, AccountCached::class), version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class CacheDatabase: RoomDatabase() {
//    abstract fun offerCachedDao(): OfferCachedDao
//    abstract fun transferCachedDao(): TransferCachedDao

    abstract fun accountCachedDao(): ConfigsCachedDao
    abstract fun accountCachedDao(): AccountCachedDao
}
