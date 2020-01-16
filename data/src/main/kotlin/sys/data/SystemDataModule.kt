package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.SimpleCacheStrategy

import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.sys.domain.*

import java.util.Locale

import org.koin.dsl.module
import sys.domain.CheckoutcomCredentials
import sys.domain.GooglePayCredentials

import kotlin.time.minutes
import kotlin.time.seconds

val systemData = module {
    /* Default models */
    single {
        Configs(
            transportTypes         = TransportType.DEFAULT_LIST,
            paymentCommission      = 2f,
            supportedCurrencies    = Currency.DEFAULT_LIST,
            supportedDistanceUnits = DistanceUnit.DEFAULT_LIST,
            availableLocales       = listOf(
                Locale("en"),
                Locale("ru"),
                Locale("de"),
                Locale("fr"),
                Locale("it"),
                Locale("es"),
                Locale("pt"),
                Locale("zh")
            ),
            contactEmails = listOf(
                ContactEmail(ContactEmail.Id.FINANCE, "finance@gettransfer.com"),
                ContactEmail(ContactEmail.Id.INFO, "info@gettransfer.com"),
                ContactEmail(ContactEmail.Id.PARTNER, "partner@gettransfer.com")
            ),
            checkoutcomCredentials =
                CheckoutcomCredentials(
                    "",
                    ""
                ),
            googlePayCredentials =
                GooglePayCredentials(
                    GooglePayCredentials.ENVIRONMENT.UNKNOWN,
                    "",
                    "",
                    listOf("AMEX", "JCB", "MASTERCARD", "VISA"),
                    listOf("CRYPTOGRAM_3DS")
                ),
            defaultCardGateway = "checkoutcom"
        )
    }
    single {
        @Suppress("MagicNumber")
        MobileConfigs(
            /*
            pushShowDelay = 5,*/
            orderMinimumMinutes = 120.minutes,
            termsUrl = "terms_of_use",
            smsResendDelaySec = 90.seconds,
            isDriverAppNotify = false,
            isDriverModeBlock = false,
            buildsConfigs = emptyMap()
        )
    }

    single {
        Preferences(
            accessToken                   = "invalid token",
            endpoint                      = null,
            ipApiKey                      = null,
            isFirstLaunch                 = true,
            isOnboardingShowed            = false,
            isNewDriverAppDialogShowed    = false,
            countOfShowNewDriverAppDialog = 0,
            selectedField                 = "",
            addressHistory                = emptyList(),
            favoriteTransports            = emptySet(),
            appEnters                     = 0,
            isDebugMenuShowed             = false,
            isPaymentRequestWithoutDelay  = false
        )
    }

    single<ConfigsRepository> {
        ConfigsRepositoryImpl(
            cacheStrategy = SimpleCacheStrategy<ConfigsEntity, Configs>(
                get<ConfigsCacheDataSource>(),
                get<ConfigsRemoteDataSource>()
            ),
            empty = get<Configs>(),
            map = { configs -> configs.map() }
        )
    }
    single<MobileConfigsRepository> {
        MobileConfigsRepositoryImpl(
            cacheStrategy = SimpleCacheStrategy<MobileConfigsEntity, MobileConfigs>(
                get<MobileConfigsCacheDataSource>(),
                get<MobileConfigsRemoteDataSource>()
            ),
            empty = get<MobileConfigs>(),
            map = { mobileConfigs -> mobileConfigs.map() }
        )
    }
    single<PreferencesRepository> {
        PreferencesRepositoryImpl(
            cache = get<PreferencesCacheDataSource>(),
            empty = get<Preferences>(),
            map = { preferences -> preferences.map() }
        )
    }
    single<EndpointRepository> { EndpointRepositoryImpl(get<EndpointRemoteDataSource>()) }
    single<IpApiKeyRepository> { IpApiKeyRepositoryImpl(get<IpApiRemoteDataSource>()) }
}
