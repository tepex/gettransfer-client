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

import com.kg.gettransfer.remote.mapper.AccountMapper as AccountRemoteMapper
import com.kg.gettransfer.remote.mapper.CardGatewaysMapper as CardGatewaysRemoteMapper
import com.kg.gettransfer.remote.mapper.CarrierTripMapper as CarrierTripRemoteMapper
import com.kg.gettransfer.remote.mapper.CarrierTripVehicleMapper as CarrierTripVehicleRemoteMapper
import com.kg.gettransfer.remote.mapper.CityPointMapper as CityPointRemoteMapper
import com.kg.gettransfer.remote.mapper.ConfigsMapper as ConfigsRemoteMapper
import com.kg.gettransfer.remote.mapper.CurrencyMapper as CurrencyRemoteMapper
import com.kg.gettransfer.remote.mapper.LocaleMapper as LocaleRemoteMapper
import com.kg.gettransfer.remote.mapper.PassengerAccountMapper as PassengerAccountRemoteMapper
import com.kg.gettransfer.remote.mapper.PaypalCredentialsMapper as PaypalCredentialsRemoteMapper
import com.kg.gettransfer.remote.mapper.RouteInfoMapper as RouteInfoRemoteMapper
import com.kg.gettransfer.remote.mapper.TransportTypeMapper as TransportTypeRemoteMapper

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module

val remoteModule = module {
    single {
        val resources = (get() as Context).resources
        ApiCore(get(), resources.getString(R.string.api_key), resources.getString(R.string.api_url))
    }
    single { RouteInfoRemoteMapper() }
    single { RouteRemoteImpl(get(), get()) as RouteRemote }
    single { AccountRemoteMapper() }
    single { TransportTypeRemoteMapper() }
    single { PaypalCredentialsRemoteMapper() }
    single { LocaleRemoteMapper() }
    single { CurrencyRemoteMapper() }
    single { CardGatewaysRemoteMapper() }
    single { ConfigsRemoteMapper(get(), get(), get(), get(), get()) }    
    single { SystemRemoteImpl(get(), get(), get()) as SystemRemote }
    
    
    single { CityPointRemoteMapper() }
    single { CarrierTripVehicleRemoteMapper() }
    single { PassengerAccountRemoteMapper() }
    single { CarrierTripRemoteMapper(get(), get(), get()) }
    single { CarrierTripRemoteImpl(get(), get()) as CarrierTripRemote }
}
