package com.kg.gettransfer.data

import com.kg.gettransfer.data.ds.*
import com.kg.gettransfer.data.ds.io.*

import com.kg.gettransfer.data.socket.CoordinateDataStoreReceiver

import com.kg.gettransfer.data.mapper.*

import com.kg.gettransfer.data.model.PlaceLocationMapper

import com.kg.gettransfer.data.repository.*

import com.kg.gettransfer.data.socket.ChatDataStoreReceiver
import com.kg.gettransfer.data.socket.OfferDataStoreReceiver
import com.kg.gettransfer.data.socket.SystemDataStoreReceiver
import com.kg.gettransfer.data.socket.PaymentDataStoreReceiver

import com.kg.gettransfer.domain.repository.*

import java.text.DateFormat
import java.text.SimpleDateFormat

import java.util.Locale

import org.koin.dsl.module.module

val dataModule = module {
    single<ThreadLocal<DateFormat>>("iso_date") {
        object: ThreadLocal<DateFormat>() {
            override protected fun initialValue(): DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        }
    }

    single<ThreadLocal<DateFormat>>("server_date") {
        object: ThreadLocal<DateFormat>() {
            override protected fun initialValue(): DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        }
    }

    single<ThreadLocal<DateFormat>>("server_time") {
        object: ThreadLocal<DateFormat>() {
            override protected fun initialValue(): DateFormat = SimpleDateFormat("HH:mm", Locale.US)
        }
    }

    single<ThreadLocal<DateFormat>>("iso_date_TZ") {
        object: ThreadLocal<DateFormat>() {
            override protected fun initialValue(): DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        }
    }

    single { LocaleMapper() }
    single { RatingsMapper() }
    single { VehicleInfoMapper() }
    single { TransportTypeMapper() }
    single { CarrierMapper() }
    single { VehicleMapper() }
    single { OfferMapper() }
    single { OfferDataStoreCache() }
    single { OfferDataStoreRemote() }
    single { OfferRepositoryImpl(DataStoreFactory<OfferDataStore, OfferDataStoreCache, OfferDataStoreRemote>(get(), get())) } bind OfferRepository::class
    single<OfferDataStoreReceiver> { OfferSocketDataStoreInput() }

    single { PaymentMapper() }
    single { PaymentRequestMapper() }
    single { BraintreeTokenMapper() }
    single { ParamsMapper() }
    single { PaymentDataStoreCache() }
    single { PaymentDataStoreRemote() }
    single { PaymentRepositoryImpl(DataStoreFactory<PaymentDataStore, PaymentDataStoreCache, PaymentDataStoreRemote>(get(), get())) } bind PaymentRepository::class

    single { PaypalCredentialsMapper() }
    single { PaymentStatusRequestMapper() }
    single { PaymentStatusMapper() }
    single { CurrencyMapper() }
    single { CardGatewaysMapper() }
    single { EndpointMapper() }
    single { AccountMapper() }
    single { AccountRegisterMapper() }
    single { ConfigsMapper() }
    single { ReviewRateMapper() }
    single { MobileConfigMapper() }
    single { BuildsConfigsMapper() }
    single { LocationMapper() }

    single { SessionDataStoreCache() }
    single { SessionDataStoreRemote() }
    single { SystemSocketDataStoreOutput(get()) }
    single <SystemDataStoreReceiver> { SystemSocketDataStoreInput() }
    single { SystemRepositoryImpl() } bind SystemRepository::class

    single { GeoDataStore() }
    single <GeoRepository> { GeoRepositoryImpl(get()) }

    single { PushTokenDataStoreRemote() }
    single { PushTokenRepositoryImpl(get()) } bind PushTokenRepository::class

    single { SocketRepositoryImpl(get()) } bind SocketRepository::class

    single { SessionRepositoryImpl(DataStoreFactory<SessionDataStore, SessionDataStoreCache, SessionDataStoreRemote>(get(), get())) } bind SessionRepository::class

    single { RouteInfoMapper() }
    single { TransportTypePriceMapper() }
    single { RouteDataStoreCache() }
    single { RouteDataStoreRemote() }
    single<RouteRepository> { RouteRepositoryImpl(DataStoreFactory<RouteDataStore, RouteDataStoreCache, RouteDataStoreRemote>(get(), get())) }

    single { DestMapper() }
    single { PassengerAccountMapper() }
    single { CarrierTripBaseMapper() }
    single { CarrierTripMapper() }
    single { CarrierTripDataStoreCache() }
    single { CarrierTripDataStoreRemote() }
    single<CarrierTripRepository> { CarrierTripRepositoryImpl(DataStoreFactory<CarrierTripDataStore, CarrierTripDataStoreCache, CarrierTripDataStoreRemote>(get(), get()), get()) }

    single { TripMapper() }
    single { TransferMapper() }
    single { TransferNewMapper() }
    single { TransferDataStoreCache() }
    single { TransferDataStoreRemote() }
    single { TransferRepositoryImpl(DataStoreFactory<TransferDataStore, TransferDataStoreCache, TransferDataStoreRemote>(get(), get())) } bind TransferRepository::class

    single { PromoDiscountMapper() }
    single { PromoDataStoreCache() }
    single { PromoDataStoreRemote() }
    single<PromoRepository> { PromoRepositoryImpl(DataStoreFactory<PromoDataStore, PromoDataStoreCache, PromoDataStoreRemote>(get(), get())) }

    single { ReviewDataStoreRemote() }
    single<ReviewRepository> { ReviewRepositoryImpl(get()) }

    single { ChatAccountMapper() }
    single { ChatMapper() }
    single { MessageMapper() }
    single { ChatBadgeEventMapper() }
    single { ChatDataStoreCache() }
    single { ChatDataStoreRemote() }
    single <ChatDataStoreReceiver> { ChatSocketDataStoreInput() }
    single { ChatSocketDataStoreOutput(get()) }
    single { ChatRepositoryImpl(DataStoreFactory<ChatDataStore, ChatDataStoreCache, ChatDataStoreRemote>(get(), get()), get()) } bind ChatRepository::class

    single <PaymentDataStoreReceiver> { PaymentSocketDataStoreInput() }

    single { CoordinateMapper() }
    single { CoordinateSocketDataStoreOutput(get()) }
    single <CoordinateDataStoreReceiver> { CoordinateSocketDataStoreInput() }
    single { CoordinateRepositoryImpl(get()) } bind CoordinateRepository::class

    single { CountEventsRepositoryImpl() } bind CountEventsRepository::class

    single { PlaceLocationMapper() }
}
