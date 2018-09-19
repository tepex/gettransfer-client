package com.kg.gettransfer.di

import android.content.Context
import android.content.SharedPreferences

import android.location.Geocoder
import android.preference.PreferenceManager

import com.google.android.gms.common.GoogleApiAvailability

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places

import com.kg.gettransfer.R

import com.kg.gettransfer.data.repository.AddressRepositoryImpl
import com.kg.gettransfer.data.repository.ApiRepositoryImpl
import com.kg.gettransfer.data.repository.LocationRepositoryImpl

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.*

import com.kg.gettransfer.domain.repository.AddressRepository
import com.kg.gettransfer.domain.repository.ApiRepository
import com.kg.gettransfer.domain.repository.LocationRepository

import java.util.Locale

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
	single { Locale.getDefault() }
	single { PreferenceManager.getDefaultSharedPreferences(get()) as SharedPreferences }
	single { LocationServices.getFusedLocationProviderClient(get<Context>()) }
	single { GoogleApiAvailability.getInstance() }
	single { Geocoder(get(), get()) }
	single { Places.getGeoDataClient(get() as Context) }
	single { Places.getPlaceDetectionClient(get() as Context) }
}

val ciceroneModule = module {
	single { Cicerone.create() as Cicerone<Router> }
	single { get<Cicerone<Router>>().router }
	single { get<Cicerone<Router>>().navigatorHolder }
}

val domainModule = module {
	single {
	    val context: Context = get()
	    ApiRepositoryImpl(context,
	                      context.resources.getString(R.string.api_url),
	                      context.resources.getString(R.string.api_key)) as ApiRepository
	}
	single { AddressRepositoryImpl(get(), get(), get()) as AddressRepository }
	single { LocationRepositoryImpl(get(), get(), get()) as LocationRepository }
	
	single { LocationInteractor(get()) }
	single { RouteInteractor(get(), get()) }
	single { SystemInteractor(get()) }
	single { TransferInteractor(get()) }
	single { CarrierTripInteractor(get()) }
}

val androidModule = module {
	single { CoroutineContexts(Dispatchers.Main, Dispatchers.IO) }
}

val testModule = module {
	single { CoroutineContexts(Dispatchers.IO, Dispatchers.IO) }
}
