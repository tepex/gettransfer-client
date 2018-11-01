package com.kg.gettransfer.di

import com.kg.gettransfer.data.*
import com.kg.gettransfer.data.mapper.PromoDiscountMapper

import com.kg.gettransfer.remote.*
import com.kg.gettransfer.remote.mapper.*

import org.koin.dsl.module.module

val remoteModule = module {
    single {
        ApiCore(get(), object: HostListener {
            override fun onAccessTokenChanged(accessToken: String) {}
            override fun onEndpointChanged(endpoint: EndpointModel, accessToken: String) {}
        })
    }
    single { PromoMapper() }
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

    single { PromoDiscountMapper() }
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
    
    single { PaymentMapper() }
    single { PaymentRequestMapper() }

    single { PaymentStatusMapper() }
    single { PaymentStatusRequestMapper() }
    single { PaymentRemoteImpl(get(), get(), get(), get(), get()) as PaymentRemote }
    single { PromoRemoteImpl(get(), get()) as PromoRemote }
}
