package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.GTAddressEntity

interface PreferencesCache {
    var accessToken: String
    var lastMode: String
    var isFirstLaunch: Boolean
    var isOnboardingShowed: Boolean
    var endpoint: EndpointEntity
    var selectedField: String
    var addressHistory: List<GTAddressEntity>
    
    val endpoints: List<EndpointEntity>

    fun logout()
    fun addListener(listener: PreferencesListener)
    fun removeListener(listener: PreferencesListener)
}
