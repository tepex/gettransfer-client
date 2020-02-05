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
import com.kg.gettransfer.presentation.delegate.PushTokenManager

import com.kg.gettransfer.presentation.mapper.CarrierMapper
import com.kg.gettransfer.presentation.mapper.PaymentStatusRequestMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.mapper.UserMapper
import com.kg.gettransfer.presentation.mapper.MessageMapper
import com.kg.gettransfer.presentation.mapper.ChatAccountMapper
import com.kg.gettransfer.presentation.mapper.ChatMapper

import com.kg.gettransfer.receiver.NetworkChangeCallback

import com.kg.gettransfer.utilities.CountryCodeManager
import com.kg.gettransfer.utilities.GTDownloadManager
import com.kg.gettransfer.utilities.GTNotificationManager
import com.kg.gettransfer.utilities.LocaleManager
import com.kg.gettransfer.utilities.LocationManager
import com.kg.gettransfer.utilities.NewTransferState

import com.kg.gettransfer.sys.presentation.ConfigsManager
import com.kg.gettransfer.sys.domain.*
import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.CommunicationManager

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
import sys.domain.SetPaymentRequestWithoutDelayInteractor

import java.util.Properties

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
        PreferencesImpl(androidContext(), get<Endpoint>().url, get())
    }
}

val domainModule = module {
    single { OfferInteractor(get()) }
    single { PaymentInteractor(get()) }
    single { OrderInteractor(get(), get(), get()) }
    single { TransferInteractor(get()) }
    single { PromoInteractor(get()) }
    single { ReviewInteractor(get()) }
    single { ChatInteractor(get()) }
    single { CoordinateInteractor(get()) }
    single { CountEventsInteractor(get()) }
    single { GeoInteractor(get(), get()) }
    single { OnesignalInteractor(get()) }
    single { SocketInteractor(get()) }
    single { SessionInteractor(get(), get()) }
}

val mappersModule = module {
    single { CarrierMapper() }
    single { PaymentStatusRequestMapper() }
    single { ProfileMapper() }
    single { UserMapper() }
    single { MessageMapper() }
    single { ChatAccountMapper() }
    single { ChatMapper() }
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
    single { NetworkChangeCallback(androidApplication().applicationContext) }
    single { PushTokenManager() }
    single { CommunicationManager() }
    single { CountryCodeManager(androidApplication().applicationContext) }
    single { LocationManager(androidApplication().applicationContext) }
}

val testModule = module {
    single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}

val systemDomain = module {
    single { GetConfigsInteractor(get()) }
    single { ClearConfigsInteractor(get()) }
    single { GetMobileConfigsInteractor(get()) }
    single { ClearMobileConfigsInteractor(get()) }
    single { GetPreferencesInteractor(get()) }
    single { IsNeedUpdateAppInteractor(get()) }
    single { IsShouldAskForRateInMarketInteractor(get()) }
    single { SetAccessTokenInteractor(get()) }
    single { SetAddressHistoryInteractor(get()) }
    single { SetAppEntersInteractor(get()) }
    single { SetDebugMenuShowedInteractor(get()) }
    single { SetEndpointInteractor(get(), get()) }
    single { SetIpApiKeyInteractor(get(), get()) }
    single { SetFavoriteTransportsInteractor(get()) }
    single { SetFirstLaunchInteractor(get()) }
    single { SetOnboardingShowedInteractor(get()) }
    single { SetSelectedFieldInteractor(get()) }
    single { SetNewDriverAppDialogShowedInteractor(get()) }
    single { AddCountOfShowNewDriverAppDialogInteractor(get()) }
    single { SetPaymentRequestWithoutDelayInteractor(get()) }
}

val systemPresentation = module {
    factory { (name: String) -> WorkerManager(name) }
    factory { (tag: String) -> LoggerFactory.getLogger(tag) }
    single { ConfigsManager() }
}

val endpoints = module {
    single<List<Endpoint>>(named(ENDPOINTS)) {
        val properties = Properties()
        val assetManager = androidContext().assets
        val inputStream = assetManager.open("gettransfer_apikey.properties")
        properties.load(inputStream)
        val prodApiKey = properties.getProperty("API_KEY_PROD")

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
                prodApiKey,
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

val ipApiKey = module {
    single<String>(named(IP_API_KEY)) { androidContext().getString(R.string.ipapi_key) }
}

const val ENDPOINTS = "endpoints"
const val IP_API_KEY = "ipapi_key"
