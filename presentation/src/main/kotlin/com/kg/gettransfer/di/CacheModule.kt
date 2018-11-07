package com.kg.gettransfer.di

import android.arch.persistence.room.Room

import com.kg.gettransfer.cache.CacheDatabase
import com.kg.gettransfer.cache.dao.*
//import com.kg.gettransfer.cache.mapper.*

import org.koin.dsl.module.module

val cacheModule = module {
    single { Room.databaseBuilder(get(), CacheDatabase::class.java, "cache.db").build() }
    /*
    single { get<CacheDatabase>().offerCachedDao() }
    single { get<CacheDatabase>().transferCachedDao() }
    */
    single { get<CacheDatabase>().accountCachedDao() }
}
