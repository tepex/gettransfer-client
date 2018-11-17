package com.kg.gettransfer.cache

import android.arch.persistence.room.Room

import com.kg.gettransfer.cache.mapper.*

import com.kg.gettransfer.data.SystemCache

import org.koin.dsl.module.module

val cacheModule = module {
    single { Room.databaseBuilder(get(), CacheDatabase::class.java, "cache.db").build() }
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
    single<SystemCache> { SystemCacheImpl() }
}
