package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.LoggingRepository
import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.Currency
import java.util.Locale

class SystemInteractor(private val systemRepository: SystemRepository,
                       private val loggingRepository: LoggingRepository,
                       private val geoRepository: GeoRepository) {
    var lastMode: String
        get() = systemRepository.lastMode
        set(value) { systemRepository.lastMode = value }
        
    var endpoint: String
        get() = systemRepository.endpoint
        set(value) { systemRepository.endpoint = value }

    lateinit var account: Account
        private set

    var locale: Locale
        get() = account.locale ?: Locale.getDefault()
        set(value) {
            account.locale = value
            geoRepository.initGeocoder(value)
        }
    var currency: Currency
        get() = account.currency ?: Currency.getInstance("USD")
        set(value) { account.currency = value }
    var distanceUnit: DistanceUnit
        get() = account.distanceUnit!!
        set(value) { account.distanceUnit = value }

    val transportTypes by lazy { systemRepository.configs.transportTypes }
    val locales        by lazy { systemRepository.configs.availableLocales }
    val distanceUnits  by lazy { systemRepository.configs.supportedDistanceUnits }
    val currencies     by lazy { systemRepository.configs.supportedCurrencies }
    
    fun isLoggedIn() = account.user.isLoggedIn()
    fun getCurrentCurrencyIndex() = currencies.indexOf(currency)

    suspend fun coldStart() {
        systemRepository.coldStart()
        account = systemRepository.getAccount()
        geoRepository.initGeocoder(locale)
    }

    fun logout() {
        systemRepository.logout()
        account = Account.NO_ACCOUNT
    }

    suspend fun login(email: String, password: String) {
        account = systemRepository.login(email, password)
    }

    suspend fun putAccount() { systemRepository.putAccount(account) }

    fun getLogs()     = loggingRepository.getLogs()
    fun clearLogs()   = loggingRepository.clearLogs()
    fun getLogsFile() = loggingRepository.getLogsFile()

    fun changeEndpoint() = systemRepository.changeEndpoint()
}
