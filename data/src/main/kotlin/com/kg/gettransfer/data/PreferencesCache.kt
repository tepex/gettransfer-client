package com.kg.gettransfer.data


import com.kg.gettransfer.data.model.AccountEntity
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
    var endpoint: String
    var isInternetAvailable: Boolean
    
    fun clearAccount()

    var lastAddresses: List<GTAddressEntity>?
}

