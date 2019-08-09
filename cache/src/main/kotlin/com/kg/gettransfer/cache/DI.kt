package com.kg.gettransfer.cache

import androidx.room.Room

import com.kg.gettransfer.data.OfferCache
import com.kg.gettransfer.data.RouteCache
import com.kg.gettransfer.data.SessionCache
import com.kg.gettransfer.data.TransferCache
import com.kg.gettransfer.data.CarrierTripCache
import com.kg.gettransfer.data.ChatCache
import com.kg.gettransfer.data.ReviewCache

import org.koin.dsl.module

val cacheModule = module {
    single {
        Room.databaseBuilder(get(), CacheDatabase::class.java, "cache.db").fallbackToDestructiveMigration().build()
    }
    single<SessionCache> { SessionCacheImpl() }
    single<TransferCache> { TransferCacheImpl() }
    single<OfferCache> { OfferCacheImpl() }
    single<RouteCache> { RouteCacheImpl() }
    single<CarrierTripCache> { CarrierTripCacheImpl() }
    single<ChatCache> { ChatCacheImpl() }
    single<ReviewCache> { ReviewCacheImpl() }
}
