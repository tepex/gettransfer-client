package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.SystemEventListener

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.PushTokenType
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.MobileConfig

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.LoggingRepository
import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.Currency
import java.util.Locale

class SystemInteractor(
    private val systemRepository: SystemRepository,
    private val loggingRepository: LoggingRepository,
    private val geoRepository: GeoRepository
) {
    /* Cached properties */

    val endpoints by lazy { systemRepository.endpoints }
    val logsFile  by lazy { loggingRepository.file }
    var locationPermissionsGranted: Boolean? = null

    /* Read only properties */

    val isInitialized: Boolean
        get() = systemRepository.isInitialized

    val accessToken: String
        get() = systemRepository.accessToken

    val account: Account
        get() = systemRepository.account

    val currentCurrencyIndex
        get() = currencies.indexOf(currency)

    val logs: String
        get() = loggingRepository.logs

    val transportTypes: List<TransportType>
        get() = systemRepository.configs.transportTypes

    val locales: List<Locale>
        get() = systemRepository.configs.availableLocales.filter { localesFilterList.contains(it.language) }

    val distanceUnits: List<DistanceUnit>
        get() = systemRepository.configs.supportedDistanceUnits

    val currencies: List<Currency> /* Dirty hack. GAA-298 */
        get() = systemRepository.configs.supportedCurrencies.filter { currenciesFilterList.contains(it.currencyCode) }

    var pushToken: String? = null
        private set

    val mobileConfigs: MobileConfig
        get() = systemRepository.mobileConfig

    /* Read-write properties */

    var lastMode: String
        get() = systemRepository.lastMode
        set(value) { systemRepository.lastMode = value }

    var lastCarrierTripsTypeView: String
        get() = systemRepository.lastCarrierTripsTypeView
        set(value) { systemRepository.lastCarrierTripsTypeView = value }

    var isFirstLaunch: Boolean
        get() = systemRepository.isFirstLaunch
        set(value) { systemRepository.isFirstLaunch = value }

    var isOnboardingShowed: Boolean
        get() = systemRepository.isOnboardingShowed
        set(value) { systemRepository.isOnboardingShowed = value }

    var selectedField: String
        get() = systemRepository.selectedField
        set(value) { systemRepository.selectedField = value }

    var endpoint: Endpoint
        get() = systemRepository.endpoint
        set(value) { systemRepository.endpoint = value }

    var addressHistory: List<GTAddress>
        get() = systemRepository.addressHistory
        set(value) { systemRepository.addressHistory = value }

    var locale: Locale
        get() = account.locale
        set(value) {
            account.locale = value
            geoRepository.initGeocoder(value)
        }

    var currency: Currency
        get() = if (currencies.contains(account.currency)) account.currency else Currency.getInstance("USD")
        set(value) { account.currency = value }

    var distanceUnit: DistanceUnit
        get() = account.distanceUnit
        set(value) { account.distanceUnit = value }

    var eventsCount: Int
        get() = systemRepository.eventsCount
        set(value) {systemRepository.eventsCount = value}

    var transferIds: List<Long>
        get() = systemRepository.transferIds
        set(value) { systemRepository.transferIds = value }

    suspend fun coldStart() = systemRepository.coldStart()

    fun initGeocoder() = geoRepository.initGeocoder(locale)

    var appEntersForMarketRate: Int
        get()  = systemRepository.appEnters
        set(value) { systemRepository.appEnters = value }

    fun logout() = systemRepository.logout()

    suspend fun registerPushToken(token: String): Result<Unit> {
        pushToken = token
        return systemRepository.registerPushToken(PushTokenType.FCM, token)
    }

    suspend fun unregisterPushToken(): Result<Boolean> {
        val result = pushToken?.let { systemRepository.unregisterPushToken(it) }
        return Result(result != null && result.error == null, result?.error)
    }

    suspend fun login(email: String, password: String) = systemRepository.login(email, password)
    suspend fun putAccount() = systemRepository.putAccount(account)
    suspend fun putNoAccount() = systemRepository.putNoAccount(account)

    fun openSocketConnection() = systemRepository.connectSocket()
    fun closeSocketConnection() = systemRepository.disconnectSocket()

    fun clearLogs() = loggingRepository.clearLogs()

    suspend fun getMyLocation() = systemRepository.getMyLocation()

    fun addListener(listener: SystemEventListener)    { systemRepository.addListener(listener) }
    fun removeListener(listener: SystemEventListener) { systemRepository.removeListener(listener) }

    companion object {
        private val currenciesFilterList = arrayOf("RUB", "THB", "USD", "GBP", "CNY", "EUR" )
        private val localesFilterList = arrayOf("en", "ru", "de")

        const val CARRIER_MODE = "carrier_mode"
    }
}
