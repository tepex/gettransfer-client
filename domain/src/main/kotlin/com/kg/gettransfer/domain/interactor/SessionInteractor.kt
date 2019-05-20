package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.MobileConfig
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.SessionRepository
import java.util.Locale

class SessionInteractor(
        private val sessionRepository: SessionRepository,
        private val geoRepository: GeoRepository
) {
    val isInitialized: Boolean
        get() = sessionRepository.isInitialized

    var accessToken: String
        get() = sessionRepository.accessToken
        set(value) { sessionRepository.accessToken = value }

    var userEmail: String
        get() = sessionRepository.userEmail
        set(value) { sessionRepository.userEmail = value }

    var userPassword: String
        get() = sessionRepository.userPassword
        set(value) { sessionRepository.userPassword = value }

    val account: Account
        get() = sessionRepository.account

    val transportTypes: List<TransportType>
        get() = sessionRepository.configs.transportTypes

    var favoriteTransports: Set<TransportType.ID>?
        get() = sessionRepository.favoriteTransportTypes
        set(value) { sessionRepository.favoriteTransportTypes = value }

    val locales: List<Locale>
        get() = sessionRepository.configs.availableLocales.filter { localesFilterList.contains(it.language) }

    val paymentCommission: Float
        get() = sessionRepository.configs.paymentCommission

    val currencies: List<Currency> /* Dirty hack. GAA-298 */
        get() = sessionRepository.configs.supportedCurrencies//.filter { currenciesFilterList.contains(it.currencyCode) }

    val mobileConfigs: MobileConfig
        get() = sessionRepository.mobileConfig

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
        get() = account.isEmailNotificationsEnabled
        set(value) { account.isEmailNotificationsEnabled = value }

    suspend fun coldStart() = sessionRepository.coldStart()

    suspend fun logout() = sessionRepository.logout()

    suspend fun login(email: String, password: String) = sessionRepository.login(email, password)
    suspend fun accountLogin(email: String?, phone: String?, password: String) = sessionRepository.accountLogin(email, phone, password)

    suspend fun getVerificationCode(email: String?, phone: String?) = sessionRepository.getVerificationCode(email, phone)

    suspend fun putAccount() = sessionRepository.putAccount(account)
    suspend fun putNoAccount() = sessionRepository.putNoAccount(account)
    suspend fun changePassword(pass: String, repeatedPass: String): Result<Account> {
        val result = sessionRepository.putAccount(account, pass, repeatedPass)
        if (result.error == null) {
            account.user.profile.email?.let { this.userEmail = it }
            this.userPassword = pass
        }
        return result
    }

    companion object {
        //private val currenciesFilterList = arrayOf("RUB", "THB", "USD", "GBP", "CNY", "EUR" )
        private val localesFilterList = arrayOf("en", "ru", "de", "es", "it", "pt", "fr", "zh")
    }
}