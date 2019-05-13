package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.SocketEventListener

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.PushTokenType
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.MobileConfig
import com.kg.gettransfer.domain.model.Currency

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.LoggingRepository
import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.Locale

class SystemInteractor(
    private val systemRepository: SystemRepository,
    private val loggingRepository: LoggingRepository,
    private val geoRepository: GeoRepository
) {
    /* Cached properties */

    val endpoints by lazy { systemRepository.endpoints }
    val logsFile  by lazy { loggingRepository.file }

    /* Read only properties */

    val isInitialized: Boolean
        get() = systemRepository.isInitialized

    var accessToken: String
        get() = systemRepository.accessToken
        set(value) { systemRepository.accessToken = value }

    var userEmail: String
        get() = systemRepository.userEmail
        set(value) { systemRepository.userEmail = value }

    var userPassword: String
        get() = systemRepository.userPassword
        set(value) { systemRepository.userPassword = value }

    val account: Account
        get() = systemRepository.account

    val currentCurrencyIndex
        get() = currencies.indexOf(currency)

    val logs: String
        get() = loggingRepository.logs

    val transportTypes: List<TransportType>
        get() = systemRepository.configs.transportTypes

    var favoriteTransports: Set<TransportType.ID>?
        get() = systemRepository.favoriteTransportTypes
        set(value) { systemRepository.favoriteTransportTypes = value }

    val locales: List<Locale>
        get() = systemRepository.configs.availableLocales.filter { localesFilterList.contains(it.language) }

    val paymentCommission: Float
        get() = systemRepository.configs.paymentCommission

    val distanceUnits: List<DistanceUnit>
        get() = systemRepository.configs.supportedDistanceUnits

    val currencies: List<Currency> /* Dirty hack. GAA-298 */
        get() = systemRepository.configs.supportedCurrencies//.filter { currenciesFilterList.contains(it.currencyCode) }

    var pushToken: String? = null
        private set

    val mobileConfigs: MobileConfig
        get() = systemRepository.mobileConfig

    /* Read-write properties */

    var lastMode: String
        get() = systemRepository.lastMode
        set(value) { systemRepository.lastMode = value }

    var lastMainScreenMode: String
        get() = systemRepository.lastMainScreenMode
        set(value) { systemRepository.lastMainScreenMode = value }

    var lastCarrierTripsTypeView: String
        get() = systemRepository.lastCarrierTripsTypeView
        set(value) { systemRepository.lastCarrierTripsTypeView = value }

    var firstDayOfWeek: Int
        get() = systemRepository.firstDayOfWeek
        set(value) { systemRepository.firstDayOfWeek = value }

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
        get() = if (currencies.contains(account.currency)) account.currency else Currency("USD", "\$")
        set(value) { account.currency = value }

    var distanceUnit: DistanceUnit
        get() = account.distanceUnit
        set(value) { account.distanceUnit = value }

    var isEmailNotificationEnabled: Boolean
        get() = account.isEmailNotificationEnabled()
    set(value) {
        account.groups =
                account.groups
                        .toMutableList()
                        .apply {
                            if (value) add(Account.GROUP_EMAIL_NOTIFICATION_PASSENGER)
                            else remove(Account.GROUP_EMAIL_NOTIFICATION_PASSENGER)
                        }
    }

    suspend fun coldStart() = systemRepository.coldStart()

    var appEntersForMarketRate: Int
        get()  = systemRepository.appEnters
        set(value) { systemRepository.appEnters = value }

    var startScreenOrder = false //for analytics

    suspend fun logout() = systemRepository.logout()

    suspend fun registerPushToken(token: String): Result<Unit> {
        pushToken = token
        systemRepository.registerPushToken(PushTokenType.FCM, token)
        return Result(Unit)
    }

    suspend fun unregisterPushToken(): Result<Unit> {
        pushToken?.let { systemRepository.unregisterPushToken(it) } ?: Result(Unit)
        return Result(Unit)
    }

    suspend fun login(email: String, password: String): Result<Account> {
        val result = systemRepository.login(email, password)
        if (result.error == null) {
            this.userEmail = email
            this.userPassword = password
        }
        return result
    }

    suspend fun accountLogin(email: String?, phone: String?, password: String): Result<Account>{
        val result = systemRepository.accountLogin(email, phone, password)
        if (email != null && result.error == null) {
            this.userEmail = email
            this.userPassword = password
        }
        return result
    }

    suspend fun getVerificationCode(email: String?, phone: String?) = systemRepository.getVerificationCode(email, phone)

    suspend fun putAccount() = systemRepository.putAccount(account)
    suspend fun putNoAccount() = systemRepository.putNoAccount(account)
    suspend fun changePassword(pass: String, repeatedPass: String): Result<Account> {
        val result = systemRepository.putAccount(account, pass, repeatedPass)
        if (result.error == null) {
            account.user.profile.email?.let { this.userEmail = it }
            this.userPassword = pass
        }
        return result
    }

    fun openSocketConnection() = systemRepository.connectSocket()
    fun closeSocketConnection() = systemRepository.disconnectSocket()

    fun clearLogs() = loggingRepository.clearLogs()

    fun addSocketListener(listener: SocketEventListener)    { systemRepository.addSocketListener(listener) }
    fun removeSocketListener(listener: SocketEventListener) { systemRepository.removeSocketListener(listener) }

    companion object {
        //private val currenciesFilterList = arrayOf("RUB", "THB", "USD", "GBP", "CNY", "EUR" )
        private val localesFilterList = arrayOf("en", "ru", "de", "es", "it", "pt", "fr", "zh")
    }
}
