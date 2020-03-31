package com.kg.gettransfer.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.kg.gettransfer.cache.dao.AccountCachedDao
import com.kg.gettransfer.cache.dao.ChatCachedDao
import com.kg.gettransfer.cache.dao.OfferCachedDao
import com.kg.gettransfer.cache.dao.RouteCacheDao
import com.kg.gettransfer.cache.dao.TransferCachedDao
import com.kg.gettransfer.cache.dao.ReviewCacheDao

import com.kg.gettransfer.cache.model.AccountCached
import com.kg.gettransfer.cache.model.ChatCached
import com.kg.gettransfer.cache.model.MessageCached
import com.kg.gettransfer.cache.model.NewMessageCached
import com.kg.gettransfer.cache.model.OfferCached
import com.kg.gettransfer.cache.model.RouteInfoCached
import com.kg.gettransfer.cache.model.TransferCached
import com.kg.gettransfer.cache.model.OfferRateCached
import com.kg.gettransfer.cache.model.OfferFeedbackCached

import com.kg.gettransfer.sys.cache.ConfigsModel
import com.kg.gettransfer.sys.cache.ConfigsDao
import com.kg.gettransfer.sys.cache.MobileConfigsModel
import com.kg.gettransfer.sys.cache.MobileConfigsDao
import com.kg.gettransfer.sys.cache.PreferencesDao
import com.kg.gettransfer.sys.cache.PreferencesModel
import com.kg.gettransfer.sys.cache.SystemConverters

@Database(entities = [
    ConfigsModel::class,
    MobileConfigsModel::class,
    PreferencesModel::class,
    AccountCached::class,
    TransferCached::class,
    OfferCached::class,
    RouteInfoCached::class,
    ChatCached::class,
    MessageCached::class,
    NewMessageCached::class,
    OfferRateCached::class,
    OfferFeedbackCached::class], version = 44, exportSchema = false)
@TypeConverters(RoomConverters::class, SystemConverters::class)
abstract class CacheDatabase : RoomDatabase() {

    abstract fun configsDao(): ConfigsDao

    abstract fun mobileConfigsDao(): MobileConfigsDao

    abstract fun preferencesDao(): PreferencesDao

    abstract fun accountCachedDao(): AccountCachedDao

    abstract fun transferCacheDao(): TransferCachedDao

    abstract fun offersCacheDao(): OfferCachedDao

    abstract fun routeCacheDao(): RouteCacheDao

    abstract fun chatCacheDao(): ChatCachedDao

    abstract fun reviewCacheDao(): ReviewCacheDao
}
