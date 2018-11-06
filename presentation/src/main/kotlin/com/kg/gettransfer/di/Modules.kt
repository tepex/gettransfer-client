package com.kg.gettransfer.di

import android.content.Context
import android.content.SharedPreferences

import android.preference.PreferenceManager

import com.google.firebase.analytics.FirebaseAnalytics

import com.google.gson.Gson

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
    single {
        val context: Context = get()
        val endpoints = listOf(
            EndpointEntity("Demo", context.resources.getString(R.string.api_key_demo), context.resources.getString(R.string.api_url_demo), true),
            EndpointEntity("Prod", context.resources.getString(R.string.api_key_prod), context.resources.getString(R.string.api_url_prod)))
        PreferencesImpl(context, endpoints)
    } bind PreferencesCache::class bind SystemCache::class
}

val loggingModule = module {
    single {
        val context: Context = get()
        LoggingRepositoryImpl(context, context.getString(R.string.logs_file_name)) as LoggingRepository
    }
}

val dataModule = module {
	single { AddressMapper() }
    single { ProfileMapper() }
    single { LocaleMapper() }
    single { RatingsMapper() }
    single { MoneyMapper() }
    single { VehicleBaseMapper() }
    single { TransportTypeMapper() }
    single { CarrierMapper(get(), get()) }
    single { PriceMapper(get()) }
    single { VehicleMapper() }
    single { OfferMapper(get(), get(), get(), get(), get()) }
    single { OfferRemoteDataStore(get()) }
    single { OfferDataStoreFactory(get()) }
	single { OfferRepositoryImpl(get(), get()) as OfferRepository }
	single { OfferInteractor(get()) }
	
	single { PaymentMapper() }
	single { PaymentRequestMapper() }
	single { PaymentRemoteDataStore(get()) }
	single { PaymentDataStoreFactory(get()) }
	single { PaymentRepositoryImpl(get(), get(), get(), get(), get()) as PaymentRepository }
	single { PaymentInteractor(get()) }
	
	single { PaypalCredentialsMapper() }
	single { PaymentStatusRequestMapper() }
	single { PaymentStatusMapper() }
	single { CurrencyMapper() }
	single { CardGatewaysMapper() }
	single { EndpointMapper() }
    single { UserMapper(get()) }
    single { AccountMapper(get()) }
    single { ConfigsMapper(get(), get(), get(), get(), get()) }
    single { SystemCacheDataStore(get()) }
    single { SystemRemoteDataStore(get()) }
    single { SystemDataStoreFactory(get(), get()) }
	single { SystemRepositoryImpl(get(), get(), get(), get(), get(), get()) as SystemRepository }
	single { SystemInteractor(get(), get(), get()) }
	
	single { RouteInfoMapper() }
	single { PointMapper() }
    single { RouteRemoteDataStore(get()) }
    single { RouteDataStoreFactory(get()) }
	single { RouteRepositoryImpl(get(), get(), get()) as RouteRepository }
	single { RouteInteractor(get(), get()) }
    
	single { CityPointMapper(get()) }
	single { PassengerAccountMapper(get()) }
	single { CarrierTripMapper(get(), get(), get()) }
	single { CarrierTripRemoteDataStore(get()) }
	single { CarrierTripDataStoreFactory(get()) }
	single { CarrierTripRepositoryImpl(get(), get()) as CarrierTripRepository }
    single { CarrierTripInteractor(get()) }
    
    single { TripMapper() }
	single { TransferMapper(get(), get()) }
	single { TransferNewMapper(get(), get(), get(), get()) }
	single { TransferCacheDataStore() }
	single { TransferRemoteDataStore(get()) }
	single { TransferDataStoreFactory(get(), get()) }
	single { TransferRepositoryImpl(get(), get(), get()) as TransferRepository }
	single { TransferInteractor(get()) }

	single { PromoRemoteDataStore(get()) }
	single { PromoDataStoreFactory(get()) }
	single { PromoRepositoryImpl(get(), get()) as PromoRepository }
	single { PromoInteractor(get()) }
}

val androidModule = module {
    factory { OfferServiceConnection(get()) } 
	single { CoroutineContexts(Dispatchers.Main, Dispatchers.IO) }
	single { FirebaseAnalytics.getInstance(androidApplication().applicationContext)  }
}

val testModule = module {
	single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}
