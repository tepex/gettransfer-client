package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.GTAddressEntity

interface PreferencesCache {
    var accessToken: String
    var userEmail: String
    var userPassword: String
    var lastMode: String
    var lastMainScreenMode: String
    var lastCarrierTripsTypeView: String
    var firstDayOfWeek: Int
    var isFirstLaunch: Boolean
    var isOnboardingShowed: Boolean
    var endpoint: EndpointEntity
    var selectedField: String
    var addressHistory: List<GTAddressEntity>
    var favoriteTransportTypes: Set<String>?
    var appEnters: Int
    var mapCountNewOffers: Map<Long, Int>
    var mapCountNewMessages: Map<Long, Int>
    var mapCountViewedOffers: Map<Long, Int>
    var eventsCount: Int
    val endpoints: List<EndpointEntity>
    var driverCoordinatesInBackGround: Int
    var offerViewExpanded: Boolean

    fun logout()
    fun addListener(listener: PreferencesListener)
    fun removeListener(listener: PreferencesListener)
}
