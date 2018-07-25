package com.kg.gettransfer

import android.content.Context
import android.content.SharedPreferences

import android.location.Geocoder
import android.preference.PreferenceManager

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer

import com.google.maps.GeoApiContext

import com.kg.gettransfer.activity.login.LoginActivity
import com.kg.gettransfer.activity.login.LoginContract
import com.kg.gettransfer.activity.login.LoginPresenter

import com.kg.gettransfer.fragment.CreateTransferFragment
import com.kg.gettransfer.fragment.TransfersFragment

import com.kg.gettransfer.module.DBProvider
import com.kg.gettransfer.module.ConfigModel
import com.kg.gettransfer.module.CurrentAccount
import com.kg.gettransfer.module.LocationModel
import com.kg.gettransfer.module.OffersModel
import com.kg.gettransfer.module.ProfileModel
import com.kg.gettransfer.module.PromoCodeModel
import com.kg.gettransfer.module.RouteInfoModel
import com.kg.gettransfer.module.Session
import com.kg.gettransfer.module.TransferModel
import com.kg.gettransfer.module.TransfersModel
import com.kg.gettransfer.module.TransportTypes

import com.kg.gettransfer.module.googleapi.GeoAutocompleteProvider
import com.kg.gettransfer.module.googleapi.GeoUtils
import com.kg.gettransfer.module.googleapi.GoogleApiClientFactory

import com.kg.gettransfer.module.http.HttpApi
import com.kg.gettransfer.module.http.HttpApiFactory
import com.kg.gettransfer.module.http.ProvideAccessTokenInterceptor

import com.kg.gettransfer.realm.secondary.ZonedDate

import org.koin.dsl.module.applicationContext

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

import timber.log.Timber

/**
 * Koin main module
 */
val AppModule = applicationContext {
	// Util
	bean { PreferenceManager.getDefaultSharedPreferences(get()) as SharedPreferences }
	// Http
	bean { RxJava2CallAdapterFactory.create() }
	bean {
		val format = "yyyy-MM-dd'T'HH:mm:ssZ"
		val df = SimpleDateFormat(format)
		df.timeZone = TimeZone.getTimeZone("UTC")
		
		val dateTypeAdapter = JsonDeserializer<Date> { json, typeOfT, context ->
			try {
				df.parse(json?.asString)
			} catch(e: java.text.ParseException) {
				Timber.e(e, "Date deserialize error: $json")
				null
			}
		}

        val zonedDateTypeAdapter = JsonDeserializer<ZonedDate> { json, typeOfT, context ->
            try {
                val s = json?.asString ?: return@JsonDeserializer null
                val date = df.parse(s)
                val tz = "GMT" + s.substring(s.length - 5)
                ZonedDate(date, tz)
            } catch (e: java.text.ParseException) {
                Timber.e(e, "Date deserialize error: $json")
                null
            }
        }

        GsonBuilder()
                .setDateFormat(format)
                .registerTypeAdapter(Date::class.java, dateTypeAdapter)
                .registerTypeAdapter(ZonedDate::class.java, zonedDateTypeAdapter)
                .excludeFieldsWithoutExposeAnnotation()
                .create() as Gson
    }
    bean { GsonConverterFactory.create(get()) as GsonConverterFactory }

    bean { ProvideAccessTokenInterceptor(get(), get(), get()) }
    bean { HttpApiFactory.buildHttpClient(get()) as okhttp3.OkHttpClient }
    bean { HttpApiFactory.create(get(), get(), get()) as HttpApi }

    bean { Session(get()) }

    bean { CurrentAccount(get(), get()) }
    bean { ProfileModel(get(), get()) }


    // Google api

    bean { GoogleApiClientFactory.create(get()) }
    bean { GeoAutocompleteProvider() }

    bean {
        GeoApiContext.Builder()
                .queryRateLimit(10)
                .apiKey(get<Context>().getString(R.string.geoapikey))
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build() as GeoApiContext
    }

    factory { GeoUtils(get(), get()) }

    bean { Geocoder(get(), Locale.getDefault()) }

    // Data
    bean { DBProvider(get()) }
    factory { get<DBProvider>().create() }

    bean { TransfersModel(get(), get(), get()) }

    bean { TransportTypes(get(), get(), get()) }

    bean { ConfigModel(get()) }


    // Models

    factory { TransferModel(get(), get()) }
    factory { OffersModel(get(), get()) }
    factory { RouteInfoModel(get(), get()) }
    factory { PromoCodeModel(get()) }
    factory { LocationModel(get(), get()) }


    // UI

    factory { CreateTransferFragment() }

    factory { LoginActivity() }
    factory { LoginPresenter(get()) as LoginContract.Presenter }

    factory { TransfersFragment() }
}

/**
 * Module list
 */
val appModules = listOf(AppModule)
