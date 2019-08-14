package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.GTAddressEntity
import com.kg.gettransfer.sys.data.EndpointEntity

interface PreferencesCache {
    var accessToken: String
    var userEmail: String?
    var userPhone: String?
    var userPassword: String
    var mapCountNewOffers: Map<Long, Int>
    var mapCountNewMessages: Map<Long, Int>
    var mapCountViewedOffers: Map<Long, Int>
    var eventsCount: Int

    fun logout()
    fun addListener(listener: PreferencesListener)
    fun removeListener(listener: PreferencesListener)
}
