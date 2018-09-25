package com.kg.gettransfer.data

import com.kg.gettransfer.domain.model.Account

interface Preferences {
    companion object {
        const val INVALID_TOKEN = "invalid_token"
    }
    
    var accessToken: String
    var account: Account
    var lastMode: String
    
    fun cleanAccount()
}
