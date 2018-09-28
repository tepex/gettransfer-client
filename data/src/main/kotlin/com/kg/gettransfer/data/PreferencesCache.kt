package com.kg.gettransfer.data

interface PreferencesCache {
    companion object {
        const val LAST_MODE = "last_mode"
        const val INVALID_TOKEN = "invalid_token"

        const val ENDPOINT_DEMO = "Demo"
        const val ENDPOINT_PROD = "Prod"
    }
    
    var accessToken: String
    var account: Account
    var lastMode: String
    var endpoint: String
    
    fun cleanAccount()
}
