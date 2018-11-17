package com.kg.gettransfer.remote.di

import com.kg.gettransfer.data.*
import com.kg.gettransfer.data.mapper.PromoDiscountMapper

import com.kg.gettransfer.remote.*
import com.kg.gettransfer.remote.mapper.*

import org.koin.dsl.module.module

import org.slf4j.LoggerFactory

val remoteModule = module {
    factory { (tag: String) -> LoggerFactory.getLogger(tag) }
    
    single { ApiCore(get()) }
    
    single { PromoMapper() }
    single { TransportTypePriceMapper() }
    single { RouteInfoMapper() }
    single<RouteRemote> { RouteRemoteImpl() }
    
    single { AccountMapper() }
    single { TransportTypeMapper() }
    single { PaypalCredentialsMapper() }
    single { LocaleMapper() }
    single { CurrencyMapper() }
    single { CardGatewaysMapper() }
    single { ConfigsMapper() }
    single { EndpointMapper() }
    single<SystemRemote> { SystemRemoteImpl() }
    
    single { CityPointMapper() }
    single { VehicleBaseMapper() }
    single { PassengerAccountMapper() }
    single { CarrierTripMapper() }
    single<CarrierTripRemote> { CarrierTripRemoteImpl() }

    single { PromoDiscountMapper() }
    single { TripMapper() }
    single { MoneyMapper() }
    single { UserMapper() }
    single { TransferMapper() }
    single { TransferNewMapper() }
    single<TransferRemote> { TransferRemoteImpl() }
    
    single { ProfileMapper() }
    single { VehicleMapper() }
    single { RatingsMapper() }
    single { CarrierMapper() }
    single { PriceMapper() }
    single { OfferMapper() }
    single<OfferRemote> { OfferRemoteImpl() }
    
    single { PaymentMapper() }
    single { PaymentRequestMapper() }
    single { PaymentStatusMapper() }
    single { PaymentStatusRequestMapper() }
    single<PaymentRemote> { PaymentRemoteImpl() }
    
    single<PromoRemote> { PromoRemoteImpl() }
}
