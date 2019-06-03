package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.User
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.MobileConfig
import com.kg.gettransfer.domain.model.DistanceUnit
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
        set(value) {
            sessionRepository.accessToken = value
        }

    val account: Account
        get() = sessionRepository.account

    val tempUser: User
        get() = sessionRepository.tempUser

    val transportTypes: List<TransportType>
        get() = sessionRepository.configs.transportTypes

    var favoriteTransports: Set<TransportType.ID>?
        get() = sessionRepository.favoriteTransportTypes
        set(value) {
            sessionRepository.favoriteTransportTypes = value
        }

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
        set(value) {
            account.currency = value
        }

    var distanceUnit: DistanceUnit
        get() = account.distanceUnit
        set(value) {
            account.distanceUnit = value
        }

    var isEmailNotificationEnabled: Boolean
        get() = account.isEmailNotificationsEnabled
        set(value) {
            account.isEmailNotificationsEnabled = value
        }

    suspend fun coldStart() = sessionRepository.coldStart()

    suspend fun logout() = sessionRepository.logout()
    suspend fun login(email: String?, phone: String?, password: String, withSmsCode: Boolean) =
        sessionRepository.login(email, phone, password, withSmsCode)

    suspend fun register(name: String, phone: String, email: String, termsAccepted: Boolean) =
        sessionRepository.register(name, phone, email, termsAccepted)

    suspend fun getVerificationCode(email: String?, phone: String?) =
        sessionRepository.getVerificationCode(email, phone)

    suspend fun putAccount(account: Account? = null) = sessionRepository.putAccount(account ?: this.account)
    suspend fun putNoAccount() = sessionRepository.putNoAccount(account)
    suspend fun changePassword(pass: String, repeatedPass: String) =
        sessionRepository.putAccount(account, pass, repeatedPass)

    companion object {
        //private val currenciesFilterList = arrayOf("RUB", "THB", "USD", "GBP", "CNY", "EUR" )
        private val localesFilterList = arrayOf("en", "ru", "de", "es", "it", "pt", "fr", "zh")
    }
}