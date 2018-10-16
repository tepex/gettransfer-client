package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Account

interface Preferences {
    companion object {
        const val INVALID_TOKEN = "invalid_token"

        const val ENDPOINT_DEMO = "Demo"
        const val ENDPOINT_PROD = "Prod"
    }
    
    var accessToken: String
    var account: Account
    var lastMode: String
    var endpoint: String
    var lastAddresses: String
    
    fun cleanAccount()
}
