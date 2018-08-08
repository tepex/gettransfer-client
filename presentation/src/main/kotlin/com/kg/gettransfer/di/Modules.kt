package com.kg.gettransfer.di

import android.content.Context
import android.content.SharedPreferences

import android.location.Geocoder
import android.preference.PreferenceManager

import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.kg.gettransfer.data.repository.LocationRepositoryImpl
import com.kg.gettransfer.domain.interactor.LocationInteractor
import com.kg.gettransfer.domain.repository.LocationRepository

import org.koin.dsl.module.module

import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

import timber.log.Timber

/**
 * Koin main module
 */
val AppModule = module {
	// Util
	single { PreferenceManager.getDefaultSharedPreferences(get()) as SharedPreferences }
	single { LocationServices.getFusedLocationProviderClient(get<Context>()) }
	single { GoogleApiAvailability.getInstance() }
}

val CiceroneModule = module {
	single { Cicerone.create() as Cicerone<Router> }
	single { get<Cicerone<Router>>().router }
	single { get<Cicerone<Router>>().navigatorHolder }
}

val DomainModule = module {
	single { LocationRepositoryImpl(get(), get(), get()) as LocationRepository}
	single { LocationInteractor(get()) }
}

val appModules = listOf(AppModule, CiceroneModule, DomainModule)
