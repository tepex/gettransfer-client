package com.kg.gettransfer.di

import android.content.Context
import android.content.SharedPreferences

import android.location.Geocoder
import android.preference.PreferenceManager

import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.kg.gettransfer.data.repository.AddressRepositoryImpl
import com.kg.gettransfer.data.repository.LocationRepositoryImpl

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.LocationInteractor

import com.kg.gettransfer.domain.repository.AddressRepository
import com.kg.gettransfer.domain.repository.LocationRepository

import java.util.Locale

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI

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
}

val ciceroneModule = module {
	single { Cicerone.create() as Cicerone<Router> }
	single { get<Cicerone<Router>>().router }
	single { get<Cicerone<Router>>().navigatorHolder }
}

val domainModule = module {
	single { AddressRepositoryImpl(get()) as AddressRepository }
	single { LocationRepositoryImpl(get(), get(), get()) as LocationRepository }
	single { AddressInteractor(get()) }
	single { LocationInteractor(get(), get()) }
}

val androidModule = module {
	single { CoroutineContexts(UI, CommonPool) }
}

/** @TODO: заменить на single thread */
val testModule = module {
	single { CoroutineContexts(CommonPool, CommonPool) }
}
