package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.GTAddressEntity
import com.kg.gettransfer.data.model.OfferEntity

interface PreferencesCache {
    var accessToken: String
    var userEmail: String
    var userPassword: String
    var lastMode: String
    var lastCarrierTripsTypeView: String
    var isFirstLaunch: Boolean
    var isOnboardingShowed: Boolean
    var endpoint: EndpointEntity
    var selectedField: String
    var addressHistory: List<GTAddressEntity>
    var appEnters: Int
    var eventsCount: Int
    var transferIds: List<Long>
    val endpoints: List<EndpointEntity>
    var driverCoordinatesInBackGround: Int

    fun logout()
    fun addListener(listener: PreferencesListener)
    fun removeListener(listener: PreferencesListener)
}
