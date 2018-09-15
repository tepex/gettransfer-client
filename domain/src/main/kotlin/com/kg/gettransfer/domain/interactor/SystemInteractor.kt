package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.domain.repository.ApiRepository

import java.util.Currency
import java.util.Locale

class SystemInteractor(private val apiRepository: ApiRepository) {
    private lateinit var configs: Configs
    lateinit var account: Account
        private set
    
    val locale = account.locale ?: Locale.getDefault()
    val distanceUnit = account.distanceUnit
    val currency = account.currency ?: Currency.getInstance(locale)
    
    val transportTypes = configs.transportTypes
    val currencies = configs.supportedCurrencies
    var currentCurrencyIndex = -1
        get() = currencies.indexOf(currency)
        private set
        
    suspend fun coldStart() {
        apiRepository.coldStart()
        configs = apiRepository.getConfigs()
        account = apiRepository.getAccount()
    }
    
    suspend fun login(email: String, password: String) = apiRepository.login(email, password)
}
