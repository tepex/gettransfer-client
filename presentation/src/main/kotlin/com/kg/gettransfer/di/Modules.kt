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

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory

import com.kg.gettransfer.R
import com.kg.gettransfer.data.Api
import com.kg.gettransfer.data.repository.AddressRepositoryImpl
import com.kg.gettransfer.data.repository.ApiRepositoryImpl
import com.kg.gettransfer.data.repository.LocationRepositoryImpl
import com.kg.gettransfer.data.repository.TransferTypeRepositoryImpl

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.interactor.LocationInteractor
import com.kg.gettransfer.domain.interactor.TransferTypeInteractor

import com.kg.gettransfer.domain.repository.AddressRepository
import com.kg.gettransfer.domain.repository.ApiRepository
import com.kg.gettransfer.domain.repository.LocationRepository
import com.kg.gettransfer.domain.repository.TransferTypeRepository

import java.util.Locale

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI

<<<<<<< HEAD
<<<<<<< HEAD
import okhttp3.CookieJar
=======
>>>>>>> added retrofit and other
=======
import okhttp3.CookieJar
>>>>>>> Api.configs() WIP
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import org.koin.dsl.module.module

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
<<<<<<< HEAD
	single { ApiRepositoryImpl(get(), (get() as Context).resources.getString(R.string.api_key)) as ApiRepository }
=======
	single { ApiRepositoryImpl(get()) as ApiRepository }
>>>>>>> added retrofit and other
	single { AddressRepositoryImpl(get(), get(), get()) as AddressRepository }
	single { LocationRepositoryImpl(get(), get(), get()) as LocationRepository }
<<<<<<< HEAD
	single { TransferTypeRepositoryImpl() as TransferTypeRepository }
=======
	single { ApiInteractor(get()) }
>>>>>>> api_key
	single { AddressInteractor(get()) }
	single { LocationInteractor(get()) }
	single { TransferTypeInteractor(get()) }
}

val apiModule = module {
	single {
		val interceptor = HttpLoggingInterceptor()
		interceptor.level = HttpLoggingInterceptor.Level.BODY
		interceptor as Interceptor
	}
	single {
		val builder = OkHttpClient.Builder()
		builder.addInterceptor(get())
<<<<<<< HEAD
<<<<<<< HEAD
		builder.cookieJar(CookieJar.NO_COOKIES)
=======
>>>>>>> added retrofit and other
=======
		builder.cookieJar(CookieJar.NO_COOKIES)
>>>>>>> Api.configs() WIP
		builder.build()
	}
	single {
		Retrofit.Builder()
		        .baseUrl((get() as Context).resources.getString(R.string.api_url))
		        .client(get())
		        .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory()) // https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter
                .build()
                .create(Api::class.java) as Api
	}
}

val androidModule = module {
	single { CoroutineContexts(UI, CommonPool) }
}

/** @TODO: заменить на single thread */
val testModule = module {
	single { CoroutineContexts(CommonPool, CommonPool) }
}
