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
        PreferencesImpl(context, endpoints) as PreferencesCache
    }
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
    single { OfferDataStoreCache(/*get()*/) }
    single { OfferDataStoreRemote(get()) }
	single { OfferRepositoryImpl(DataStoreFactory<OfferDataStore, OfferDataStoreCache, OfferDataStoreRemote>(get(), get()), get()) as OfferRepository }
	single { OfferInteractor(get()) }
	
	single { PaymentMapper() }
	single { PaymentRequestMapper() }
	single { PaymentDataStoreCache(/*get()*/) }
	single { PaymentDataStoreRemote(get()) }
	single { PaymentRepositoryImpl(DataStoreFactory<PaymentDataStore, PaymentDataStoreCache, PaymentDataStoreRemote>(get(), get()), get(), get(), get(), get()) as PaymentRepository }
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
    single { SystemDataStoreCache(get()) }
    single { SystemDataStoreRemote(get()) }
	single { SystemRepositoryImpl(DataStoreFactory<SystemDataStore, SystemDataStoreCache, SystemDataStoreRemote>(get(), get()),
	                              get(), get(), get(), get(), get()) as SystemRepository }
	single { SystemInteractor(get(), get(), get()) }
	
	single { RouteInfoMapper() }
	single { PointMapper() }
	single { RouteDataStoreCache(/*get()*/) }
    single { RouteDataStoreRemote(get()) }
	single { RouteRepositoryImpl(DataStoreFactory<RouteDataStore, RouteDataStoreCache, RouteDataStoreRemote>(get(), get()), get(), get()) as RouteRepository }
	single { RouteInteractor(get(), get()) }
    
	single { CityPointMapper(get()) }
	single { PassengerAccountMapper(get()) }
	single { CarrierTripMapper(get(), get(), get()) }
	single { CarrierTripDataStoreCache(/*get()*/) }
	single { CarrierTripDataStoreRemote(get()) }
	single { CarrierTripRepositoryImpl(DataStoreFactory<CarrierTripDataStore, CarrierTripDataStoreCache, CarrierTripDataStoreRemote>(get(), get()), get()) as CarrierTripRepository }
    single { CarrierTripInteractor(get()) }
    
    single { TripMapper() }
	single { TransferMapper(get(), get()) }
	single { TransferNewMapper(get(), get(), get(), get()) }
	single { TransferDataStoreCache(/*get()*/) }
	single { TransferDataStoreRemote(get()) }
	single { TransferRepositoryImpl(DataStoreFactory<TransferDataStore, TransferDataStoreCache, TransferDataStoreRemote>(get(), get()), get(), get()) as TransferRepository }
	single { TransferInteractor(get()) }

	single { PromoDataStoreCache(/*get()*/) }
	single { PromoDataStoreRemote(get()) }
	single { PromoRepositoryImpl(DataStoreFactory<PromoDataStore, PromoDataStoreCache, PromoDataStoreRemote>(get(), get()), get()) as PromoRepository }
	single { PromoInteractor(get()) }
}

val androidModule = module {
    factory { OfferServiceConnection(get()) } 
	single { CoroutineContexts(Dispatchers.Main, Dispatchers.IO) }
	single { FirebaseAnalytics.getInstance(androidApplication().applicationContext)  }
	single { LocaleManager() }
	single { AppEventsLogger.newLogger(androidApplication().applicationContext) }
}

val testModule = module {
	single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}
