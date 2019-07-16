package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.domain.repository.SystemRepository

import java.util.Locale

class SystemInteractor(private val systemRepository: SystemRepository) {

    val endpoints by lazy { systemRepository.endpoints }

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

    var appEntersForMarketRate: Int
        get()  = systemRepository.appEnters
        set(value) { systemRepository.appEnters = value }

    var startScreenOrder = false // for analytics

    var isDebugMenuShowed: Boolean
        get() = systemRepository.isDebugMenuShowed
        set(value) { systemRepository.isDebugMenuShowed = value }

    var accessToken: String
        get() = systemRepository.accessToken
        set(value) {
            systemRepository.accessToken = value
        }

    val transportTypes: List<TransportType>
        get() = systemRepository.configs.transportTypes

    var favoriteTransports: Set<TransportType.ID>?
        get() = systemRepository.favoriteTransportTypes
        set(value) {
            systemRepository.favoriteTransportTypes = value
        }

    val locales: List<Locale>
        get() = systemRepository.configs.availableLocales.filter { localesFilterList.contains(it.language) }

    val paymentCommission: Float
        get() = systemRepository.configs.paymentCommission

    /* Dirty hack. GAA-298 */
    val currencies: List<Currency>
        get() = systemRepository.configs.supportedCurrencies

    companion object {
        // private val currenciesFilterList = arrayOf("RUB", "THB", "USD", "GBP", "CNY", "EUR" )
        private val localesFilterList = arrayOf("en", "ru", "de", "es", "it", "pt", "fr", "zh")
    }
}
