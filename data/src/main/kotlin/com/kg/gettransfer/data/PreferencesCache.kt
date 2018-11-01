package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.GTAddressEntity

interface PreferencesCache {
    companion object {
        const val LAST_MODE     = "last_mode"
        const val ENDPOINT      = "endpoint"
        const val INVALID_TOKEN = "invalid_token"
        const val INTERNET      = "internet_available"
    }
    
    var accessToken: String
    var account: AccountEntity
    var lastMode: String
    var endpoint: EndpointEntity
    var isInternetAvailable: Boolean
    
    fun clearAccount()

    val endpoints: List<EndpointEntity>
    var lastAddresses: List<GTAddressEntity>
    
    fun addListener(listener: PreferencesListener)
    fun removeListener(listener: PreferencesListener)
}
