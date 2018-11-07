package com.kg.gettransfer.cache

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

import com.kg.gettransfer.cache.dao.OfferCachedDao
import com.kg.gettransfer.cache.dao.TransferCachedDao
import com.kg.gettransfer.cache.model.OfferCached
import com.kg.gettransfer.cache.model.TransferCached

@Database(entities = [OfferCached::class, TransferCached::class], version = 1)
abstract class CacheDatabase: RoomDatabase() {
    abstract fun offerCachedDao(): OfferCachedDao
    abstract fun transferCachedDao() : TransferCachedDao
}
