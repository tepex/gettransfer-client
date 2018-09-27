package com.kg.gettransfer.di

import android.content.Context
import android.content.SharedPreferences

import android.preference.PreferenceManager

import com.kg.gettransfer.R

import com.kg.gettransfer.data.prefs.PreferencesImpl

import com.kg.gettransfer.data.repository.*

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.*
import com.kg.gettransfer.domain.repository.*

import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.android.Main

import org.koin.dsl.module.module

import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

import timber.log.Timber

/**
 * Koin main module
 */
val appModule = module {
	// Util
	single { PreferenceManager.getDefaultSharedPreferences(get()) as SharedPreferences }
}

val ciceroneModule = module {
	single { Cicerone.create() as Cicerone<Router> }
	single { get<Cicerone<Router>>().router }
	single { get<Cicerone<Router>>().navigatorHolder }
}

val domainModule = module {
    single { PreferencesImpl(get()) as Preferences }
	single {
	    val context: Context = get()
	    ApiRepositoryImpl(get(),
	                      context.resources.getString(R.string.api_key),
	                      context.resources.getString(R.string.api_url))
	}
	single { CarrierTripRepositoryImpl(get()) as CarrierTripRepository }
	single { GeoRepositoryImpl(get()) as GeoRepository }
	single { RouteRepositoryImpl(get()) as RouteRepository }
	single { SystemRepositoryImpl(get()) as SystemRepository }
	single { TransferRepositoryImpl(get()) as TransferRepository }
	
	single { CarrierTripInteractor(get()) }
	single { RouteInteractor(get(), get()) }
	single { SystemInteractor(get(), get(), get()) }
	single { TransferInteractor(get()) }
}

val androidModule = module {
	single { CoroutineContexts(Dispatchers.Main, Dispatchers.IO) }
}

val testModule = module {
	single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}
