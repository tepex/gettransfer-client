package com.kg.gettransfer.remote

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.ChatRemote
import com.kg.gettransfer.data.GeoRemote
import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.PaymentRemote
import com.kg.gettransfer.data.PromoRemote
import com.kg.gettransfer.data.PushTokenRemote
import com.kg.gettransfer.data.ReviewRemote
import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.SessionRemote
import com.kg.gettransfer.data.TransferRemote

import com.kg.gettransfer.data.socket.ChatEventEmitter
import com.kg.gettransfer.data.socket.CoordinateEventEmitter
import com.kg.gettransfer.data.socket.SystemEventEmitter

import com.kg.gettransfer.remote.socket.ChatSocketImpl
import com.kg.gettransfer.remote.socket.CoordinateSocketImpl
import com.kg.gettransfer.remote.socket.OfferSocketImpl
import com.kg.gettransfer.remote.socket.PaymentSocketEventer
import com.kg.gettransfer.remote.socket.SocketManager
import com.kg.gettransfer.remote.socket.SystemSocketImp

import org.koin.dsl.bind
import org.koin.dsl.module

val remoteModule = module {
    single { ApiCore() }

    single<RouteRemote> { RouteRemoteImpl() }
    single<SessionRemote> { SessionRemoteImpl() }

    single<CarrierTripRemote> { CarrierTripRemoteImpl() }
    single<TransferRemote> { TransferRemoteImpl() }
    single<OfferRemote> { OfferRemoteImpl() }
    single<PaymentRemote> { PaymentRemoteImpl() }
    single<PromoRemote> { PromoRemoteImpl() }
    single<ChatRemote> { ChatRemoteImpl() }
    single<ReviewRemote> { ReviewRemoteImpl() }
    single<GeoRemote> { GeoRemoteImpl() }
    single<PushTokenRemote> { PushTokenRemoteImpl() }
}

val socketModule = module {
    single { SocketManager() }

    single { CoordinateSocketImpl() } bind CoordinateEventEmitter::class
    single { SystemSocketImp() } bind SystemEventEmitter::class
    single { OfferSocketImpl(get()) }
    single { ChatSocketImpl() }
    single { PaymentSocketEventer() }
    single<ChatEventEmitter> { ChatSocketImpl() }
}
