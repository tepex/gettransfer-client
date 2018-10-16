package com.kg.gettransfer.di

import android.content.Context

import com.kg.gettransfer.R

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.SystemRemote

import com.kg.gettransfer.remote.ApiCore
import com.kg.gettransfer.remote.CarrierTripRemoteImpl
import com.kg.gettransfer.remote.RouteRemoteImpl
import com.kg.gettransfer.remote.SystemRemoteImpl

import com.kg.gettransfer.remote.mapper.*

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

val remoteModule = module {
    single { ApiCore(get()) }
    single { RouteInfoMapper() }
    single { RouteRemoteImpl(get(), get()) as RouteRemote }
    single { AccountMapper() }
    single { TransportTypeMapper() }
    single { PaypalCredentialsMapper() }
    single { LocaleMapper() }
    single { CurrencyMapper() }
    single { CardGatewaysMapper() }
    single { ConfigsMapper(get(), get(), get(), get(), get()) }
    single { EndpointMapper() }
    single { SystemRemoteImpl(get(), get(), get(), get()) as SystemRemote }
    
    single { CityPointMapper() }
    single { VehicleBaseMapper() }
    single { PassengerAccountMapper() }
    single { CarrierTripMapper(get(), get(), get()) }
    single { CarrierTripRemoteImpl(get(), get()) as CarrierTripRemote }
}
