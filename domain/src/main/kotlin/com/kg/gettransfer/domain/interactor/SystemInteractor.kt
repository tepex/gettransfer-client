package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.domain.repository.ApiRepository

import java.util.Currency
import java.util.Locale

class SystemInteractor(private val apiRepository: ApiRepository) {
    lateinit var lastMode: String
    private lateinit var configs: Configs
    lateinit var account: Account
        private set
    
    suspend fun coldStart() {
        apiRepository.coldStart()
        configs = apiRepository.getConfigs()
        account = apiRepository.getAccount()
        lastMode = apiRepository.getLastMode()
    }
    
    fun getTransportTypes()       = configs.transportTypes
    fun getLocales()              = configs.availableLocales
    fun getDistanceUnits()        = configs.supportedDistanceUnits
    fun getCurrencies()           = configs.supportedCurrencies
    fun getCurrentCurrencyIndex() = getCurrencies().indexOf(getCurrency()) 
    
    fun getLocale()       = account.locale ?: Locale.getDefault()
    fun getDistanceUnit() = account.distanceUnit
    fun getCurrency()     = account.currency ?: Currency.getInstance("USD")
    
    fun logout() { apiRepository.logout() }
    suspend fun login(email: String, password: String) = apiRepository.login(email, password)
    suspend fun putAccount() { apiRepository.putAccount(account) }
    fun getAccount() { account = apiRepository.getAccount() }
    fun putLastMode(mode: String) { apiRepository.putLastMode(mode) }
}
