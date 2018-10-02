package com.kg.gettransfer.di

import android.content.Context
import android.content.SharedPreferences

import android.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.R
import com.kg.gettransfer.data.logging.LoggingImpl

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.SystemRemote

import com.kg.gettransfer.data.ds.*
import com.kg.gettransfer.data.mapper.*
import com.kg.gettransfer.data.repository.*

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.*
import com.kg.gettransfer.domain.repository.*

import com.kg.gettransfer.prefs.PreferencesImpl

import com.kg.gettransfer.remote.ApiCore
import com.kg.gettransfer.remote.SystemRemoteImpl

import com.kg.gettransfer.remote.mapper.AccountMapper as EntityAccountMapper
import com.kg.gettransfer.remote.mapper.ConfigsMapper as EntityConfigsMapper

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
val appModule = module {
	// Util
	//single { PreferenceManager.getDefaultSharedPreferences(get()) as SharedPreferences }
}

val ciceroneModule = module {
	single { Cicerone.create() as Cicerone<Router> }
	single { get<Cicerone<Router>>().router }
	single { get<Cicerone<Router>>().navigatorHolder }
}

val domainModule = module {
    single { PreferencesImpl(get()) } bind PreferencesCache::class bind SystemCache::class
    
    single {
        val resources = (get() as Context).resources
        ApiCore(get(), resources.getString(R.string.api_key), resources.getString(R.string.api_url))
    }
    single { SystemRemoteImpl(get(), EntityConfigsMapper(), EntityAccountMapper()) as SystemRemote }
    
    single { SystemCacheDataStore(get()) }
    single { SystemRemoteDataStore(get()) }
    single { SystemDataStoreFactory(get(), get()) }
    single { AccountMapper() }
    single { ConfigsMapper() }
    
	single {
		val context: Context = get()
		LoggingImpl(get(),
				context.getString(R.string.logs_file_name)) as Logging}
	single {
	    val context: Context = get()
	    ApiRepositoryImpl(get(),
                context.resources.getStringArray(R.array.api_keys),
				context.resources.getStringArray(R.array.api_urls))
	}
	
	single { CarrierTripRepositoryImpl(get()) as CarrierTripRepository }
	single { GeoRepositoryImpl(get()) as GeoRepository }
	single { OfferRepositoryImpl(get()) as OfferRepository }
	single { PaymentRepositoryImpl(get()) as PaymentRepository }
	single { RouteRepositoryImpl(get()) as RouteRepository }
	single { SystemRepositoryImpl(get(), get(), get(), get()) as SystemRepository }
	single { TransferRepositoryImpl(get()) as TransferRepository }
	
	single { CarrierTripInteractor(get()) }
	single { OfferInteractor(get()) }
	single { PaymentInteractor(get()) }
	single { RouteInteractor(get(), get()) }
	single { SystemInteractor(get(), get()) }
	single { TransferInteractor(get()) }
}

val androidModule = module {
	single { CoroutineContexts(Dispatchers.Main, Dispatchers.IO) }
	single { FirebaseAnalytics.getInstance(androidApplication().applicationContext)  }
}

val testModule = module {
	single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}
