package com.kg.gettransfer.di

import android.content.Context
import android.content.SharedPreferences

import android.preference.PreferenceManager

import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R

import com.kg.gettransfer.logging.LoggingRepositoryImpl

import com.kg.gettransfer.data.CarrierTripRemote
import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.RouteRemote
import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.SystemRemote

import com.kg.gettransfer.data.ds.*
import com.kg.gettransfer.data.mapper.*
import com.kg.gettransfer.data.repository.*

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.*
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.repository.*

import com.kg.gettransfer.geo.GeoRepositoryImpl
import com.kg.gettransfer.prefs.PreferencesImpl

import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.android.Main

import org.koin.android.ext.koin.androidApplication

import org.koin.dsl.module.module

import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

/**
 * Koin main module
 */
val ciceroneModule = module {
    single { Cicerone.create() as Cicerone<Router> }
    single { get<Cicerone<Router>>().router }
    single { get<Cicerone<Router>>().navigatorHolder }
}

val geoModule = module {
    single { GeoRepositoryImpl(get()) as GeoRepository }
}

val prefsModule = module {
    single { PreferencesImpl(get()) } bind PreferencesCache::class bind SystemCache::class
}

val loggingModule = module {
    single {
        val context: Context = get()
        LoggingRepositoryImpl(get(), context.getString(R.string.logs_file_name)) as LoggingRepository
    }
}

val dataModule = module {
    single { ApiRepositoryImpl(get()) }
    
    single { ProfileMapper() }
    single { OfferMapper(get(), get(), get(), get(), get()) }
    single { OfferDataStoreFactory(get()) }
	single { OfferRepositoryImpl(get(), get()) as OfferRepository }
	single { OfferInteractor(get()) }
	
	single { PaymentRepositoryImpl(get()) as PaymentRepository }
	single { PaymentInteractor(get()) }
	
	single { TransferRepositoryImpl(get()) as TransferRepository }
	single { TransferInteractor(get()) }
	
	single { TransportTypeMapper() }
	single { PaypalCredentialsMapper() }
	single { LocaleMapper() }
	single { CurrencyMapper() }
	single { CardGatewaysMapper() }
	single { EndpointMapper() }
    single { UserMapper(get()) }
    single { AccountMapper(get()) }
    single { ConfigsMapper(get(), get(), get(), get(), get()) }
    single { SystemCacheDataStore(get()) }
    single { SystemRemoteDataStore(get()) }
    single { SystemDataStoreFactory(get(), get()) }
	single {
	    val context: Context = get()
	    val endpoints = arrayListOf(
	        Endpoint("Demo", context.resources.getString(R.string.api_key_demo), context.resources.getString(R.string.api_url_demo), true),
	        Endpoint("Prod", context.resources.getString(R.string.api_key_prod), context.resources.getString(R.string.api_url_prod)))
	    SystemRepositoryImpl(get(), get(), get(), get(), get(), endpoints) as SystemRepository
	}
	single { SystemInteractor(get(), get(), get()) }
	
	single { RouteInfoMapper() }
	single { PointMapper() }
    single { RouteRemoteDataStore(get()) }
    single { RouteDataStoreFactory(get()) }
	single { RouteRepositoryImpl(get(), get(), get()) as RouteRepository }
	single { RouteInteractor(get(), get()) }
    
	single { CityPointMapper(get()) }
	single { VehicleBaseMapper() }
	single { PassengerAccountMapper(get()) }
	single { CarrierTripMapper(get(), get(), get()) }
	single { CarrierTripRepositoryImpl(get(), get()) as CarrierTripRepository }
    single { CarrierTripInteractor(get()) }
}

val androidModule = module {
	single { CoroutineContexts(Dispatchers.Main, Dispatchers.IO) }
	single { FirebaseAnalytics.getInstance(androidApplication().applicationContext)  }
}

val testModule = module {
	single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}
