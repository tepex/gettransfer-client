package com.kg.gettransfer.data

interface PreferencesCache {
    var endpointUrl: String
    var accessToken: String
    var userEmail: String?
    var userPhone: String?
    var userPassword: String?
    var mapCountNewOffers: Map<Long, Int>
    var mapCountNewMessages: Map<Long, Int>
    var mapCountViewedOffers: Map<Long, Int>
    var eventsCount: Int
    var appLanguage: String
    var isAppLanguageChanged: Boolean

    fun logout()
}
