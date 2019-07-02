package com.kg.gettransfer.di

import android.content.Context

import com.facebook.appevents.AppEventsLogger

import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R
import com.kg.gettransfer.data.Location
import com.kg.gettransfer.utilities.LocaleManager

import com.kg.gettransfer.logging.LoggingRepositoryImpl

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.*
import com.kg.gettransfer.domain.repository.*

import com.kg.gettransfer.prefs.EncryptPass

import com.kg.gettransfer.prefs.PreferencesImpl
import com.kg.gettransfer.presentation.FileLoggingTree

import com.kg.gettransfer.encrypt.EncryptPassImpl
import com.kg.gettransfer.geo.LocationImpl
import com.kg.gettransfer.presentation.delegate.AccountManager
import com.kg.gettransfer.presentation.delegate.DateTimeDelegate
import com.kg.gettransfer.presentation.delegate.PassengersDelegate

import com.kg.gettransfer.presentation.mapper.*

import com.kg.gettransfer.utilities.Analytics

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import com.kg.gettransfer.utilities.GTNotificationManager
import com.kg.gettransfer.utilities.MainState

import kotlinx.coroutines.Dispatchers

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext

import org.koin.dsl.module

import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

/**
 * Koin main module
 */
val ciceroneModule = module {
    single<Cicerone<Router>> { Cicerone.create() }
    single { get<Cicerone<Router>>().router }
    single { get<Cicerone<Router>>().navigatorHolder }
}

val geoModule = module {
    single<Location> { LocationImpl(androidContext()) }
}

val encryptModule = module {
    single<EncryptPass> { EncryptPassImpl() }
}

val prefsModule = module {
    single<PreferencesCache> {
        val prodEndpointName = androidContext().getString(R.string.endpoint_prod)
        val demoEndpointName = androidContext().getString(R.string.endpoint_demo)

        val endpoints = listOf(
                EndpointEntity(
                        demoEndpointName,
                        androidContext().getString(R.string.api_key_demo),
                        androidContext().getString(R.string.api_url_demo), true),

                EndpointEntity(
                        prodEndpointName,
                        androidContext().getString(R.string.api_key_prod),
                        androidContext().getString(R.string.api_url_prod)))

        var defaultEndpointName = prodEndpointName
        if (BuildConfig.FLAVOR == "dev") defaultEndpointName = demoEndpointName

        PreferencesImpl(androidContext(), endpoints, defaultEndpointName, get())
    }
}

val loggingModule = module {
    single { LoggingRepositoryImpl(androidContext(), FileLoggingTree.LOGGER_NAME) as LoggingRepository }
}

const val FILE_LIMIT = 1024 * 1024  //1 Mb
const val FILES_COUNT = 1
val fileModule = module {
    single {
        Logger.getLogger(FileLoggingTree.LOGGER_NAME).also { l ->
        FileHandler(androidContext().filesDir.path.toString().plus("/${l.name}"), FILE_LIMIT, FILES_COUNT, true).also { h ->
            h.formatter = SimpleFormatter()
            l.addHandler(h)
       } } }
}

val domainModule = module {
    single { OfferInteractor(get()) }
    single { PaymentInteractor(get()) }
    single { SystemInteractor(get()) }
    single { OrderInteractor(get(), get(), get()) }
    single { CarrierTripInteractor(get()) }
    single { TransferInteractor(get()) }
    single { PromoInteractor(get()) }
    single { ReviewInteractor(get()) }
    single { ChatInteractor(get()) }
    single { CoordinateInteractor(get()) }
    single { CountEventsInteractor(get()) }
    single { GeoInteractor(get(), get()) }
    single { PushTokenInteractor(get()) }
    single { SocketInteractor(get()) }
    single { LogsInteractor(get()) }
    single { SessionInteractor(get(), get(), get()) }
}

val mappersModule = module {
    single { BookNowOfferMapper() }
    single { CarrierMapper() }
    single { CarrierTripBaseMapper() }
    single { CarrierTripMapper() }
    single { CarrierTripsListItemsMapper() }
    single { CarrierTripsCalendarItemsMapper() }
    single { EndpointMapper() }
    single { MoneyMapper() }
    single { OfferMapper() }
    single { PassengerAccountMapper() }
    single { PaymentRequestMapper() }
    single { PaymentStatusRequestMapper() }
    single { PointMapper() }
    single { PriceMapper() }
    single { ProfileMapper() }
    single { ReviewRateMapper() }
    single { RouteMapper() }
    single { TransferMapper() }
    single { UserMapper() }
    single { VehicleInfoMapper() }
    single { VehicleMapper() }
    single { MessageMapper() }
    single { ChatAccountMapper() }
    single { ChatMapper() }
    single { CityPointMapper() }
}

val androidModule = module {
    single { CoroutineContexts(Dispatchers.Main, Dispatchers.IO) }
    single { FirebaseAnalytics.getInstance(androidApplication().applicationContext) }
    single { LocaleManager() }
    single { AppEventsLogger.newLogger(androidApplication().applicationContext) }
    single { Analytics(androidApplication().applicationContext, get(), get()) }
    single { PhoneNumberUtil.createInstance(get<Context>()) }
    single { GTNotificationManager(androidApplication().applicationContext) }
    single { DateTimeDelegate() }
    single { PassengersDelegate() }
    single { MainState() }
    single { AccountManager() }
}

val testModule = module {
    single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}
