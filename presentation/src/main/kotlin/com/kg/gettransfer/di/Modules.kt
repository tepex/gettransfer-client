package com.kg.gettransfer.di

import android.content.Context
import android.content.SharedPreferences

import android.preference.PreferenceManager
import com.facebook.appevents.AppEventsLogger

import com.google.firebase.analytics.FirebaseAnalytics

import com.google.gson.Gson

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R
import com.kg.gettransfer.utilities.LocaleManager

import com.kg.gettransfer.logging.LoggingRepositoryImpl

import com.kg.gettransfer.data.CarrierTripDataStore
import com.kg.gettransfer.data.OfferDataStore
import com.kg.gettransfer.data.PaymentDataStore
import com.kg.gettransfer.data.PromoDataStore
import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.RouteDataStore
import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.SystemDataStore
import com.kg.gettransfer.data.TransferDataStore

import com.kg.gettransfer.data.ds.*
import com.kg.gettransfer.data.mapper.*
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.repository.*

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.*
import com.kg.gettransfer.domain.repository.*

import com.kg.gettransfer.geo.GeoRepositoryImpl

import com.kg.gettransfer.prefs.PreferencesImpl

import com.kg.gettransfer.service.OfferServiceConnection

import kotlinx.coroutines.Dispatchers

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext

import org.koin.dsl.module.applicationContext
import org.koin.dsl.module.module

import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

import timber.log.Timber

/**
 * Koin main module
 */
val ciceroneModule = module {
    single<Cicerone<Router>> { Cicerone.create() }
    single { get<Cicerone<Router>>().router }
    single { get<Cicerone<Router>>().navigatorHolder }
}

val geoModule = module {
    single<GeoRepository> { GeoRepositoryImpl(get()) }
}

val prefsModule = module {
    single<PreferencesCache> {
        val endpoints = if(BuildConfig.FLAVOR == "prod" || BuildConfig.FLAVOR == "home") listOf(
            EndpointEntity("Prod", androidContext().getString(R.string.api_key_prod), androidContext().getString(R.string.api_url_prod)))
        else listOf(
            EndpointEntity("Demo", androidContext().getString(R.string.api_key_demo), androidContext().getString(R.string.api_url_demo), true),
            EndpointEntity("Prod", androidContext().getString(R.string.api_key_prod), androidContext().getString(R.string.api_url_prod)))
        PreferencesImpl(androidContext(), endpoints)
    }
}

val loggingModule = module {
    single { LoggingRepositoryImpl(androidContext(), androidContext().getString(R.string.logs_file_name)) as LoggingRepository }
}

val domainModule = module {
    single { OfferInteractor(get()) }
    single { PaymentInteractor(get()) }
    single { SystemInteractor(get(), get(), get()) }
    single { RouteInteractor(get(), get()) }
    single { CarrierTripInteractor(get()) }
    single { TransferInteractor(get()) }
    single { PromoInteractor(get()) }
}

val androidModule = module {
    factory { OfferServiceConnection() }
	single { CoroutineContexts(Dispatchers.Main, Dispatchers.IO) }
	single { FirebaseAnalytics.getInstance(androidApplication().applicationContext) }
	single { LocaleManager() }
	single { AppEventsLogger.newLogger(androidApplication().applicationContext) }
}

val testModule = module {
	single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}
