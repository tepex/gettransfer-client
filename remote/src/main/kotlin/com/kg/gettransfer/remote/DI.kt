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

val remoteMappersModule = module {
    single { AccountMapper() }
    single { BookNowOfferMapper() }

    single { CardGatewaysMapper() }
    single { CarrierMapper() }
    single { CarrierTripBaseMapper() }
    single { CarrierTripMapper() }
    single { CityPointMapper() }
    single { ConfigsMapper() }
    single { CurrencyMapper() }

    single { EndpointMapper() }
    single { LocaleMapper() }
    single { MoneyMapper() }

    single { OfferMapper() }

    single { PassengerAccountMapper() }
    single { PaymentMapper() }
    single { PaymentRequestMapper() }
    single { PaymentStatusMapper() }
    single { PaymentStatusRequestMapper() }
    single { PaypalCredentialsMapper() }
    single { PriceMapper() }
    single { ProfileMapper() }
    single { PromoMapper() }

    single { RatingsMapper() }
    single { RouteInfoMapper() }

    single { TransferMapper() }
    single { TransferNewMapper() }
    single { TransportTypeMapper() }
    single { TransportTypePriceMapper() }
    single { TripMapper() }

    single { UserMapper() }
    single { VehicleInfoMapper() }
    single { VehicleMapper() }
}

val remoteModule = module {
    factory { (tag: String) -> LoggerFactory.getLogger(tag) }

    single { ApiCore() }
    single<RouteRemote> { RouteRemoteImpl() }
    single<SystemRemote> { SystemRemoteImpl() }
    single<CarrierTripRemote> { CarrierTripRemoteImpl() }
    single<TransferRemote> { TransferRemoteImpl() }
    single<OfferRemote> { OfferRemoteImpl() }
    single<PaymentRemote> { PaymentRemoteImpl() }
    single<PromoRemote> { PromoRemoteImpl() }
}
