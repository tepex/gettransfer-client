package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.Currency
import java.util.Locale

class SystemInteractor(private val systemRepository: SystemRepository,
                       private val geoRepository: GeoRepository) {
    private lateinit var configs: Configs
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
        get() = account.distanceUnit
        set(value) { account.distanceUnit = value }

    val transportTypes       = systemRepository.getConfigs().transportTypes
    val locales              = systemRepository.getConfigs().availableLocales
    val distanceUnits        = systemRepository.getConfigs().supportedDistanceUnits
    val currencies           = systemRepository.getConfigs().supportedCurrencies
    val currentCurrencyIndex = currencies.indexOf(account.currency)
    val loggedIn             = account.email != null

    var endpoint: String
        get() = systemRepository.getEndpoint()
        set(value) { systemRepository.setEndpoint(value) }

    suspend fun coldStart() {
        systemRepository.coldStart()
        account = systemRepository.getAccount()
        geoRepository.initGeocoder(locale)
    }
    
    fun getTransportTypes()       = configs.transportTypes
    fun getLocales()              = configs.availableLocales
    fun getDistanceUnits()        = configs.supportedDistanceUnits
    fun getCurrencies()           = configs.supportedCurrencies
    fun getEndpoints()            = systemRepository.getEndpoins()
    fun getCurrentCurrencyIndex() = getCurrencies().indexOf(account.currency)
    fun isLoggedIn()              = account.user.isLoggedIn()
    
    fun logout() {
        systemRepository.logout()
        account = systemRepository.getAccount()
    }

    suspend fun login(email: String, password: String) {
        account = systemRepository.login(email, password)
    }
    
    suspend fun putAccount() { systemRepository.putAccount(account) }

    fun getLogs()     = systemRepository.getLogs()
    fun clearLogs()   = systemRepository.clearLogs()
    fun getLogsFile() = systemRepository.getLogsFile()

    fun changeEndpoint() = systemRepository.changeEndpoint()
}
