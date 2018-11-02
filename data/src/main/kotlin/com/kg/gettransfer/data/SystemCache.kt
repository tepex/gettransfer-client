package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

interface SystemCache {
    companion object {
        const val INVALID_TOKEN = "invalid_token"
    }

    var account: AccountEntity
    var configs: ConfigsEntity
    
    fun clearAccount()
}
