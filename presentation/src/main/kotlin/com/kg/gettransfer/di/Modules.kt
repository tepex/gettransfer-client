package com.kg.gettransfer.di

import android.content.Context

import com.facebook.appevents.AppEventsLogger

import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.data.Location
import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.*

import com.kg.gettransfer.prefs.EncryptPass

import com.kg.gettransfer.prefs.PreferencesImpl

import com.kg.gettransfer.encrypt.EncryptPassImpl
import com.kg.gettransfer.geo.LocationImpl
import com.kg.gettransfer.presentation.delegate.AccountManager
import com.kg.gettransfer.presentation.delegate.DateTimeDelegate
import com.kg.gettransfer.presentation.delegate.PassengersDelegate

import com.kg.gettransfer.presentation.mapper.*
import com.kg.gettransfer.utilities.*

import com.kg.gettransfer.sys.presentation.ConfigsManager
import com.kg.gettransfer.sys.data.EndpointEntity
import com.kg.gettransfer.sys.domain.*

import com.kg.gettransfer.utilities.Analytics

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext

import org.koin.core.qualifier.named
import org.koin.dsl.module

import org.slf4j.LoggerFactory

import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

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
        /*
        val prodEndpointName = androidContext().getString(R.string.endpoint_prod)
        val demoEndpointName = androidContext().getString(R.string.endpoint_demo)
        val devEndpointName = androidContext().getString(R.string.endpoint_dev)

        val endpoints = listOf(
            EndpointEntity(
                demoEndpointName,
                androidContext().getString(R.string.api_key_demo),
                androidContext().getString(R.string.api_url_demo), true),
            EndpointEntity(
                prodEndpointName,
                androidContext().getString(R.string.api_key_prod),
                androidContext().getString(R.string.api_url_prod)),
            EndpointEntity(
                devEndpointName,
                androidContext().getString(R.string.api_key_dev),
                androidContext().getString(R.string.api_url_dev),
                false, isDev = true)
        )

        var defaultEndpointName = prodEndpointName
        if (BuildConfig.FLAVOR == "dev") defaultEndpointName = demoEndpointName
*/
        PreferencesImpl(androidContext(),/* endpoints, defaultEndpointName,*/ get())
    }
}

val domainModule = module {
    single { OfferInteractor(get()) }
    single { PaymentInteractor(get()) }
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
    single { SessionInteractor(get(), get()) }
}

val mappersModule = module {
    single { CarrierMapper() }
    single { CarrierTripBaseMapper() }
    single { CarrierTripMapper() }
    single { CarrierTripsListItemsMapper() }
    single { CarrierTripsCalendarItemsMapper() }
    single { OfferMapper() }
    single { PassengerAccountMapper() }
    single { PaymentRequestMapper() }
    single { PaymentStatusRequestMapper() }
    single { PointMapper() }
    single { ProfileMapper() }
    single { RouteMapper() }
    single { UserMapper() }
    single { VehicleInfoMapper() }
    single { MessageMapper() }
    single { ChatAccountMapper() }
    single { ChatMapper() }
    single { CityPointMapper() }
}

val androidModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    single { CoroutineContexts(Dispatchers.Main, Dispatchers.IO) }
    single { FirebaseAnalytics.getInstance(androidApplication().applicationContext) }
    single { LocaleManager() }
    single { AppEventsLogger.newLogger(androidApplication().applicationContext) }
    single { Analytics(androidApplication().applicationContext, get(), get()) }
    single { PhoneNumberUtil.createInstance(get<Context>()) }
    single { GTNotificationManager(androidApplication().applicationContext) }
    single { DateTimeDelegate() }
    single { PassengersDelegate() }
    single { NewTransferState() }
    single { AccountManager() }
    single { GTDownloadManager(androidApplication().applicationContext) }
}

val testModule = module {
    single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}

val systemDomain = module {
    single { GetConfigsInteractor(get()) }
    single { GetMobileConfigsInteractor(get()) }
    single { GetPreferencesInteractor(get()) }
    single { IsNeedUpdateAppInteractor(get()) }
    single { IsShouldAskForRateInMarketInteractor(get()) }
    single { SetAccessTokenInteractor(get()) }
    single { SetAddressHistoryInteractor(get()) }
    single { SetAppEntersInteractor(get()) }
    single { SetDebugMenuShowedInteractor(get()) }
    single { SetEndpointInteractor(get(), get()) }
    single { SetFavoriteTransportsInteractor(get()) }
    single { SetFirstDayOfWeekInteractor(get()) }
    single { SetFirstLaunchInteractor(get()) }
    single { SetLastCarrierTripsTypeViewInteractor(get()) }
    single { SetLastModeInteractor(get()) }
    single { SetOnboardingShowedInteractor(get()) }
    single { SetSelectedFieldInteractor(get()) }
    single { SetBackgroundCoordinatesInteractor(get()) }
}

val systemPresentation = module {
    factory { (name: String) -> WorkerManager(name) }
    factory { (tag: String) -> LoggerFactory.getLogger(tag) }
    single { ConfigsManager() }
}

val endpoints = module {
    single<List<Endpoint>>(named(ENDPOINTS)) {
        listOf(
            Endpoint(
                androidContext().getString(R.string.endpoint_demo),
                androidContext().getString(R.string.api_key_demo),
                androidContext().getString(R.string.api_url_demo),
                true,
                false
            ),
            Endpoint(
                androidContext().getString(R.string.endpoint_prod),
                androidContext().getString(R.string.api_key_prod),
                androidContext().getString(R.string.api_url_prod),
                false,
                false
            ),
            Endpoint(
                androidContext().getString(R.string.endpoint_dev),
                androidContext().getString(R.string.api_key_dev),
                androidContext().getString(R.string.api_url_dev),
                false,
                true
            )
        )
    }
    single<Endpoint> {
        val endpoints = get<List<Endpoint>>(named(ENDPOINTS))
        if (BuildConfig.FLAVOR == "dev") endpoints[0] else endpoints[1]
    }
}

const val ENDPOINTS = "endpoints"
