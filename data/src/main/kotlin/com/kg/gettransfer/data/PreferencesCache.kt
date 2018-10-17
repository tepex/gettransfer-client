package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity

interface PreferencesCache {
    companion object {
        const val LAST_MODE     = "last_mode"
        const val ENDPOINT      = "endpoint"
        const val INVALID_TOKEN = "invalid_token"        
    }
    
    var accessToken: String
    var account: AccountEntity
    var lastMode: String
    var endpoint: String
    
    fun clearAccount()
}
