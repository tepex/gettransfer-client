package com.kg.gettransfer.data

import com.kg.gettransfer.data.ds.*
import com.kg.gettransfer.data.socket.TransferDataStoreReceiver
import com.kg.gettransfer.data.mapper.*
import com.kg.gettransfer.data.repository.*
import com.kg.gettransfer.data.socket.ChatDataStoreReceiver
import com.kg.gettransfer.data.socket.OfferDataStoreReceiver

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

    single { AddressMapper() }
    single { ProfileMapper() }
    single { LocaleMapper() }
    single { RatingsMapper() }
    single { MoneyMapper() }
    single { VehicleInfoMapper() }
    single { TransportTypeMapper() }
    single { CarrierMapper() }
    single { PriceMapper() }
    single { VehicleMapper() }
    single { OfferMapper() }
    single { OfferDataStoreCache() }
    single { OfferDataStoreRemote() }
    single { OfferRepositoryImpl(DataStoreFactory<OfferDataStore, OfferDataStoreCache, OfferDataStoreRemote>(get(), get())) } bind OfferRepository::class
    single<OfferDataStoreReceiver> { OfferDataStoreIO() }

    single { PaymentMapper() }
    single { PaymentRequestMapper() }
    single { PaymentDataStoreCache() }
    single { PaymentDataStoreRemote() }
    single<PaymentRepository> { PaymentRepositoryImpl(DataStoreFactory<PaymentDataStore, PaymentDataStoreCache, PaymentDataStoreRemote>(get(), get())) }

    single { PaypalCredentialsMapper() }
    single { PaymentStatusRequestMapper() }
    single { PaymentStatusMapper() }
    single { CurrencyMapper() }
    single { CardGatewaysMapper() }
    single { EndpointMapper() }
    single { UserMapper() }
    single { AccountMapper() }
    single { ConfigsMapper() }
    single { ReviewRateMapper() }
    single { MobileConfigMapper() }
    single { LocationMapper() }

    single { SystemDataStoreCache() }
    single { SystemDataStoreRemote() }
    single { SystemDataStoreIO(get()) }
    single<SystemRepository> { SystemRepositoryImpl(DataStoreFactory<SystemDataStore, SystemDataStoreCache, SystemDataStoreRemote>(get(), get()), get()) }

    single { RouteInfoMapper() }
    single { PointMapper() }
    single { TransportTypePriceMapper() }
    single { RouteDataStoreCache() }
    single { RouteDataStoreRemote() }
    single<RouteRepository> { RouteRepositoryImpl(DataStoreFactory<RouteDataStore, RouteDataStoreCache, RouteDataStoreRemote>(get(), get())) }

    single { CityPointMapper() }
    single { DestMapper() }
    single { PassengerAccountMapper() }
    single { CarrierTripBaseMapper() }
    single { CarrierTripMapper() }
    single { CarrierTripDataStoreCache() }
    single { CarrierTripDataStoreRemote() }
    single<CarrierTripRepository> { CarrierTripRepositoryImpl(DataStoreFactory<CarrierTripDataStore, CarrierTripDataStoreCache, CarrierTripDataStoreRemote>(get(), get())) }

    single { BookNowOfferMapper() }
    single { TripMapper() }
    single { TransferMapper() }
    single { TransferNewMapper() }
    single { TransferDataStoreCache() }
    single { TransferDataStoreRemote() }
    single<TransferDataStoreReceiver> { TransferDataStoreIO(get()) }
    single<TransferRepository> { TransferRepositoryImpl(DataStoreFactory<TransferDataStore, TransferDataStoreCache, TransferDataStoreRemote>(get(), get())) }

    single { PromoDiscountMapper() }
    single { PromoDataStoreCache() }
    single { PromoDataStoreRemote() }
    single<PromoRepository> { PromoRepositoryImpl(DataStoreFactory<PromoDataStore, PromoDataStoreCache, PromoDataStoreRemote>(get(), get())) }

    single { ReviewDataStoreRemote() }
    single<ReviewRepository> { ReviewRepositoryImpl(get()) }

    single<ChatDataStoreReceiver> { ChatDataStoreIO(get()) }

    single { ChatAccountMapper() }
    single { ChatMapper() }
    single { MessageMapper() }
    single { ChatDataStoreCache() }
    single { ChatDataStoreRemote() }
    single<ChatRepository> { ChatRepositoryImpl(DataStoreFactory<ChatDataStore, ChatDataStoreCache, ChatDataStoreRemote>(get(), get())) }
}
