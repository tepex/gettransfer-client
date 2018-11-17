package com.kg.gettransfer.remote

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.PaymentRemote
import com.kg.gettransfer.data.PromoRemote
import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.SystemRemote
import com.kg.gettransfer.data.TransferRemote

import com.kg.gettransfer.remote.mapper.*

import org.koin.dsl.module.module

import org.slf4j.LoggerFactory

val remoteModule = module {
    factory { (tag: String) -> LoggerFactory.getLogger(tag) }
    
    single { ApiCore(get()) }
    
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
    
    single { PromoMapper() }
    single<PromoRemote> { PromoRemoteImpl() }
}
