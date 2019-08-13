package com.kg.gettransfer.sys.remote

import com.kg.gettransfer.sys.data.ConfigsRemoteDataSource
import com.kg.gettransfer.sys.data.EndpointRemoteDataSource
import com.kg.gettransfer.sys.data.MobileConfigsRemoteDataSource

import org.koin.dsl.module

val systemRemote = module {
    single { SystemApiWrapper() }
    single<EndpointRemoteDataSource> { EndpointRemote() }
    single<ConfigsRemoteDataSource> { ConfigsRemote() }
    single<MobileConfigsRemoteDataSource> { MobileConfigsRemote() }
}
