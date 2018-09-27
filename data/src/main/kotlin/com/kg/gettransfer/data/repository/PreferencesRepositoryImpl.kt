package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.repository.PreferencesRepository

class PreferencesRepositoryImpl(private val cache: PreferencesCache): PreferencesRepository {
    internal var accessToken: String
        get() = cache.accessToken
        set(value) { cache.accessToken = value }
        
    internal var account: Account
        get() = cache.account
        set(value) { cache.account = value }
        
    override var lastMode: String
        get() = cache.lastMode
        set(value) { cache.lastMode = value }
        
    internal fun cleanAccount() = cache.cleanAccount()
}
