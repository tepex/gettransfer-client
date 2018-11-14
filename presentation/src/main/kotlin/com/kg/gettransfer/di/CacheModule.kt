package com.kg.gettransfer.di

import android.arch.persistence.room.Room

import com.kg.gettransfer.cache.CacheDatabase
import com.kg.gettransfer.cache.SystemCacheImpl

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
    single { ConfigsEntityMapper(get(), get(), get(), get(), get()) }
    single { AccountEntityMapper() }
    single { SystemCacheImpl(get(), get(), get()) as SystemCache }
}
