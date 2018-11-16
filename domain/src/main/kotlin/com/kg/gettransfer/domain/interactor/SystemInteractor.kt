package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.SystemListener

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.LoggingRepository
import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.Currency
import java.util.Locale
/**
 * Nothing cache properties here!
 */
class SystemInteractor(private val systemRepository: SystemRepository,
                       private val loggingRepository: LoggingRepository,
                       private val geoRepository: GeoRepository) {

    /* Cached properties */

    val endpoints      by lazy { systemRepository.endpoints }
    val transportTypes by lazy { systemRepository.configs.transportTypes }
    val locales        by lazy { systemRepository.configs.availableLocales }
    val distanceUnits  by lazy { systemRepository.configs.supportedDistanceUnits }
    val currencies     by lazy { systemRepository.configs.supportedCurrencies }
    val logsFile       by lazy { loggingRepository.file }

    /* Read only properties */

    val accessToken: String
        get() = systemRepository.accessToken

    val account: Account
        get() = systemRepository.account

    val currentCurrencyIndex
        get() = currencies.indexOf(currency)

    val logs: String
        get() = loggingRepository.logs

    /* Read-write properties */
    
    var lastMode: String
        get() = systemRepository.lastMode
        set(value) { systemRepository.lastMode = value }

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
        get() = account.currency
        set(value) { account.currency = value }

    var distanceUnit: DistanceUnit
        get() = account.distanceUnit
        set(value) { account.distanceUnit = value }

    /** Init geo with account.locale if retrieved from remote */
    suspend fun coldStart() = systemRepository.coldStart().apply { geoRepository.initGeocoder(model.locale) }

    fun logout() = systemRepository.logout()
    suspend fun login(email: String, password: String) = systemRepository.login(email, password)
    suspend fun putAccount() = systemRepository.putAccount(account)
    
    fun clearLogs() = loggingRepository.clearLogs()
    
    fun addListener(listener: SystemListener)    { systemRepository.addListener(listener) }
    fun removeListener(listener: SystemListener) { systemRepository.removeListener(listener) }
}
