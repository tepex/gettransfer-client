package com.kg.gettransfer.sys.cache

import com.kg.gettransfer.sys.data.ConfigsCacheDataSource
import com.kg.gettransfer.sys.data.MobileConfigsCacheDataSource
import com.kg.gettransfer.sys.data.PreferencesCacheDataSource

import org.koin.dsl.module

val systemCache = module {
    single<ConfigsCacheDataSource> { ConfigsCache() }
    single<MobileConfigsCacheDataSource> { MobileConfigsCache() }
    single<PreferencesCacheDataSource> { PreferencesCache() }
}
