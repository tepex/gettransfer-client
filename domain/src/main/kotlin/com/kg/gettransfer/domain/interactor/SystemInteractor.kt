package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.DistanceUnit

import com.kg.gettransfer.domain.repository.ApiRepository
import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.Preferences

import java.util.Currency
import java.util.Locale

class SystemInteractor(private val apiRepository: ApiRepository,
                       private val geoRepository: GeoRepository,
                       private val preferences: Preferences) {
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
        get() = account.distanceUnit ?: DistanceUnit.Km
        set(value) { account.distanceUnit = value }

    var lastMode: String
        get() = preferences.lastMode
        set(value) { preferences.lastMode = value }

    suspend fun coldStart() {
        apiRepository.coldStart()
        configs = apiRepository.getConfigs()
        account = apiRepository.getAccount()
        geoRepository.initGeocoder(locale)
    }
    
    fun getTransportTypes()       = configs.transportTypes
    fun getLocales()              = configs.availableLocales
    fun getDistanceUnits()        = configs.supportedDistanceUnits
    fun getCurrencies()           = configs.supportedCurrencies
    fun getCurrentCurrencyIndex() = getCurrencies().indexOf(account.currency)
    fun isLoggedIn()              = account.email != null
    
    fun logout() {
        apiRepository.logout()
        account = apiRepository.getAccount()
    }
    
    suspend fun login(email: String, password: String) {
        account = apiRepository.login(email, password)
    }
    
    suspend fun putAccount() { apiRepository.putAccount(account) }
}
