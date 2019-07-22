package com.kg.gettransfer.data

import com.kg.gettransfer.data.ds.CarrierTripDataStoreCache
import com.kg.gettransfer.data.ds.CarrierTripDataStoreRemote
import com.kg.gettransfer.data.ds.ChatDataStoreCache
import com.kg.gettransfer.data.ds.ChatDataStoreRemote
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.GeoDataStore
import com.kg.gettransfer.data.ds.OfferDataStoreCache
import com.kg.gettransfer.data.ds.OfferDataStoreRemote
import com.kg.gettransfer.data.ds.PaymentDataStoreCache
import com.kg.gettransfer.data.ds.PaymentDataStoreRemote
import com.kg.gettransfer.data.ds.PromoDataStoreCache
import com.kg.gettransfer.data.ds.PromoDataStoreRemote
import com.kg.gettransfer.data.ds.PushTokenDataStoreRemote
import com.kg.gettransfer.data.ds.ReviewDataStoreCache
import com.kg.gettransfer.data.ds.ReviewDataStoreRemote
import com.kg.gettransfer.data.ds.RouteDataStoreCache
import com.kg.gettransfer.data.ds.RouteDataStoreRemote
import com.kg.gettransfer.data.ds.SessionDataStoreCache
import com.kg.gettransfer.data.ds.SessionDataStoreRemote
import com.kg.gettransfer.data.ds.SystemDataStoreCache
import com.kg.gettransfer.data.ds.SystemDataStoreRemote
import com.kg.gettransfer.data.ds.TransferDataStoreCache
import com.kg.gettransfer.data.ds.TransferDataStoreRemote

import com.kg.gettransfer.data.ds.io.ChatSocketDataStoreInput
import com.kg.gettransfer.data.ds.io.ChatSocketDataStoreOutput
import com.kg.gettransfer.data.ds.io.CoordinateSocketDataStoreInput
import com.kg.gettransfer.data.ds.io.CoordinateSocketDataStoreOutput
import com.kg.gettransfer.data.ds.io.OfferSocketDataStoreInput
import com.kg.gettransfer.data.ds.io.PaymentSocketDataStoreInput
import com.kg.gettransfer.data.ds.io.SystemSocketDataStoreInput
import com.kg.gettransfer.data.ds.io.SystemSocketDataStoreOutput

import com.kg.gettransfer.data.repository.CarrierTripRepositoryImpl
import com.kg.gettransfer.data.repository.ChatRepositoryImpl
import com.kg.gettransfer.data.repository.CoordinateRepositoryImpl
import com.kg.gettransfer.data.repository.CountEventsRepositoryImpl
import com.kg.gettransfer.data.repository.GeoRepositoryImpl
import com.kg.gettransfer.data.repository.MobileConfigsRepositoryImpl
import com.kg.gettransfer.data.repository.OfferRepositoryImpl
import com.kg.gettransfer.data.repository.PaymentRepositoryImpl
import com.kg.gettransfer.data.repository.PromoRepositoryImpl
import com.kg.gettransfer.data.repository.PushTokenRepositoryImpl
import com.kg.gettransfer.data.repository.ReviewRepositoryImpl
import com.kg.gettransfer.data.repository.RouteRepositoryImpl
import com.kg.gettransfer.data.repository.SessionRepositoryImpl
import com.kg.gettransfer.data.repository.SocketRepositoryImpl
import com.kg.gettransfer.data.repository.SystemRepositoryImpl
import com.kg.gettransfer.data.repository.TransferRepositoryImpl

import com.kg.gettransfer.data.socket.ChatDataStoreReceiver
import com.kg.gettransfer.data.socket.CoordinateDataStoreReceiver
import com.kg.gettransfer.data.socket.OfferDataStoreReceiver
import com.kg.gettransfer.data.socket.PaymentDataStoreReceiver
import com.kg.gettransfer.data.socket.SystemDataStoreReceiver

import com.kg.gettransfer.domain.repository.CarrierTripRepository
import com.kg.gettransfer.domain.repository.ChatRepository
import com.kg.gettransfer.domain.repository.CoordinateRepository
import com.kg.gettransfer.domain.repository.CountEventsRepository
import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.OfferRepository
import com.kg.gettransfer.domain.repository.PaymentRepository
import com.kg.gettransfer.domain.repository.PromoRepository
import com.kg.gettransfer.domain.repository.PushTokenRepository
import com.kg.gettransfer.domain.repository.ReviewRepository
import com.kg.gettransfer.domain.repository.RouteRepository
import com.kg.gettransfer.domain.repository.SessionRepository
import com.kg.gettransfer.domain.repository.SocketRepository
import com.kg.gettransfer.domain.repository.SystemRepository
import com.kg.gettransfer.domain.repository.TransferRepository

import com.kg.gettransfer.core.domain.Repository

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.bind

val dataModule = module {
    single<ThreadLocal<DateFormat>>(named("iso_date")) {
        object : ThreadLocal<DateFormat>() {
            override fun initialValue(): DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        }
    }

    single<ThreadLocal<DateFormat>>(named("server_date")) {
        object : ThreadLocal<DateFormat>() {
            override fun initialValue(): DateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        }
    }

    single<ThreadLocal<DateFormat>>(named("server_time")) {
        object : ThreadLocal<DateFormat>() {
            override fun initialValue(): DateFormat = SimpleDateFormat("HH:mm", Locale.US)
        }
    }

    single<ThreadLocal<DateFormat>>(named("iso_date_TZ")) {
        object : ThreadLocal<DateFormat>() {
            override fun initialValue(): DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        }
    }

    single { OfferDataStoreCache() }
    single { OfferDataStoreRemote() }
    single { OfferRepositoryImpl(DataStoreFactory(get(), get()), get()) } bind OfferRepository::class
    single<OfferDataStoreReceiver> { OfferSocketDataStoreInput() }

    single { PaymentDataStoreCache() }
    single { PaymentDataStoreRemote() }
    single { PaymentRepositoryImpl(DataStoreFactory(get(), get())) } bind PaymentRepository::class

    single { SystemSocketDataStoreOutput(get()) }
    single <SystemDataStoreReceiver> { SystemSocketDataStoreInput() }
    single { SystemDataStoreCache() }
    single { SystemDataStoreRemote() }
    single { SystemRepositoryImpl(DataStoreFactory(get(), get())) } bind SystemRepository::class

    single { GeoDataStore() }
    single <GeoRepository> { GeoRepositoryImpl(get()) }

    single { PushTokenDataStoreRemote() }
    single { PushTokenRepositoryImpl(get()) } bind PushTokenRepository::class

    single { MobileConfigsRepositoryImpl() } bind Repository::class

    single { SocketRepositoryImpl(get()) } bind SocketRepository::class

    single { SessionDataStoreCache() }
    single { SessionDataStoreRemote() }
    single { SessionRepositoryImpl(DataStoreFactory(get(), get())) } bind SessionRepository::class

    single { RouteDataStoreCache() }
    single { RouteDataStoreRemote() }
    single<RouteRepository> { RouteRepositoryImpl(DataStoreFactory(get(), get())) }

    single { CarrierTripDataStoreCache() }
    single { CarrierTripDataStoreRemote() }
    single<CarrierTripRepository> { CarrierTripRepositoryImpl(DataStoreFactory(get(), get()), get()) }

    single { TransferDataStoreCache() }
    single { TransferDataStoreRemote() }
    single { TransferRepositoryImpl(DataStoreFactory(get(), get())) } bind TransferRepository::class

    single { PromoDataStoreCache() }
    single { PromoDataStoreRemote() }
    single<PromoRepository> { PromoRepositoryImpl(DataStoreFactory(get(), get())) }

    single { ReviewDataStoreCache() }
    single { ReviewDataStoreRemote() }
    single<ReviewRepository> { ReviewRepositoryImpl(DataStoreFactory(get(), get())) }

    single { ChatDataStoreCache() }
    single { ChatDataStoreRemote() }
    single <ChatDataStoreReceiver> { ChatSocketDataStoreInput() }
    single { ChatSocketDataStoreOutput(get()) }
    single { ChatRepositoryImpl(DataStoreFactory(get(), get()), get()) } bind ChatRepository::class

    single <PaymentDataStoreReceiver> { PaymentSocketDataStoreInput() }

    single { CoordinateSocketDataStoreOutput(get()) }
    single <CoordinateDataStoreReceiver> { CoordinateSocketDataStoreInput() }
    single { CoordinateRepositoryImpl(get()) } bind CoordinateRepository::class

    single { CountEventsRepositoryImpl() } bind CountEventsRepository::class
}
