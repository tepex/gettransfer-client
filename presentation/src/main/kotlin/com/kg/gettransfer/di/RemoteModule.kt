package com.kg.gettransfer.di

import android.content.Context

import com.kg.gettransfer.R

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.SystemRemote
import com.kg.gettransfer.data.TransferRemote

import com.kg.gettransfer.remote.ApiCore
import com.kg.gettransfer.remote.CarrierTripRemoteImpl
import com.kg.gettransfer.remote.OfferRemoteImpl
import com.kg.gettransfer.remote.RouteRemoteImpl
import com.kg.gettransfer.remote.SystemRemoteImpl
import com.kg.gettransfer.remote.TransferRemoteImpl

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
    
    single { TripMapper() }
    single { MoneyMapper() }
    single { UserMapper() }
    single { TransferMapper(get(), get()) }
    single { TransferNewMapper(get(), get(), get(), get()) }
    single { TransferRemoteImpl(get(), get(), get()) as TransferRemote }
    
    single { ProfileMapper() }
    single { VehicleMapper() }
    single { RatingsMapper() }
    single { CarrierMapper(get(), get()) }
    single { PriceMapper(get()) }
    single { OfferMapper(get(), get(), get(), get(), get()) }
    single { OfferRemoteImpl(get(), get()) as OfferRemote }
}
