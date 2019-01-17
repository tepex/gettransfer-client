package com.kg.gettransfer.cache

import android.arch.persistence.room.Room

import com.kg.gettransfer.cache.mapper.*

import com.kg.gettransfer.data.OfferCache
import com.kg.gettransfer.data.RouteCache
import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.TransferCache
import com.kg.gettransfer.data.CarrierTripCache

import org.koin.dsl.module.module

val cacheModule = module {
    single {
        Room.databaseBuilder(get(), CacheDatabase::class.java, "cache.db")
            .fallbackToDestructiveMigration()
            .build() 
    }
    /*
    single { get<CacheDatabase>().offerCachedDao() }
    single { get<CacheDatabase>().transferCachedDao() }
    */
    
    single { CardGatewaysEntityMapper() }
    single { CurrencyEntityMapper() }
    single { LocaleEntityMapper() }
    single { PaypalCredentialsEntityMapper() }
    single { TransportTypeEntityMapper() }
    single { ConfigsEntityMapper() }
    single { AccountEntityMapper() }
    single { TransferEntityMapper() }
    single { BookNowOfferEntityMapper() }
    single { CityPointEntityMapper() }
    single { MoneyEntityMapper() }
    single { PriceEntityMapper() }
    single { RatingsEntityMapper() }
    single { CarrierEntityMapper() }
    single { ProfileEntityMapper() }
    single { VehicleEntityMapper() }
    single { OfferEntityMapper() }
    single { RouteInfoEntityMapper() }
    single { VehicleInfoEntityMapper() }
    single { CarrierTripBaseEntityMapper() }
    single { PassengerAccountEntityMapper() }
    single { CarrierTripEntityMapper() }
    single { ChatAccountEntityMapper() }
    single { ChatEntityMapper() }
    single { MessageEntityMapper() }
    single<SystemCache> { SystemCacheImpl() }
    single<TransferCache> { TransferCacheImpl() }
    single<OfferCache> { OfferCacheImpl() }
    single<RouteCache> { RouteCacheImpl() }
    single<CarrierTripCache> { CarrierTripCacheImpl() }
}
