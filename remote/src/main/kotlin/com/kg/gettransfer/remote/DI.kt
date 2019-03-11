package com.kg.gettransfer.remote

import com.kg.gettransfer.data.*
import com.kg.gettransfer.data.socket.ChatEventEmitter
import com.kg.gettransfer.data.socket.SystemEventEmitter
import com.kg.gettransfer.data.socket.CoordinateEventEmitter

import com.kg.gettransfer.remote.mapper.*
import com.kg.gettransfer.remote.socket.*

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
    single { MobileConfigMapper() }
    single { CurrencyMapper() }

    single { EndpointMapper() }
    single { LocaleMapper() }
    single { MoneyMapper() }

    single { OfferMapper() }

    single { PassengerAccountMapper() }

    single { ReviewRateMapper() }

    single { PaymentMapper() }
    single { PaymentRequestMapper() }
    single { PaymentStatusMapper() }
    single { PaymentStatusRequestMapper() }
    single { PaypalCredentialsMapper() }
    single { BraintreeTokenMapper() }
    single { ParamsMapper() }
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

    single { ChatAccountMapper() }
    single { MessageMapper() }
    single { ChatMapper() }

    single { LocationMapper() }
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
    single<ChatRemote> { ChatRemoteImpl() }

    single<ReviewRemote> { ReviewRemoteImpl() }

}

val socketModule = module {
    single { SocketManager() }

    single { CoordinateSocketImpl() } bind CoordinateEventEmitter::class
    single { SystemSocketImp() } bind SystemEventEmitter::class
    single { OfferSocketImpl(get()) }
    single { ChatSocketImpl() }
    single<ChatEventEmitter> { ChatSocketImpl() }
}
