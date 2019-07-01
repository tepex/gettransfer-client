package com.kg.gettransfer.cache

import android.arch.persistence.room.Room

import com.kg.gettransfer.data.OfferCache
import com.kg.gettransfer.data.RouteCache
import com.kg.gettransfer.data.SessionCache
import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.TransferCache
import com.kg.gettransfer.data.CarrierTripCache
import com.kg.gettransfer.data.ChatCache

import org.koin.dsl.module

val cacheModule = module {
    single {
        Room.databaseBuilder(get(), CacheDatabase::class.java, "cache.db").fallbackToDestructiveMigration().build()
    }
    single<SessionCache> { SessionCacheImpl() }
    single<SystemCache> { SystemCacheImpl() }
    single<TransferCache> { TransferCacheImpl() }
    single<OfferCache> { OfferCacheImpl() }
    single<RouteCache> { RouteCacheImpl() }
    single<CarrierTripCache> { CarrierTripCacheImpl() }
    single<ChatCache> { ChatCacheImpl() }
}
