package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

interface SystemRepository {
    var configs: Configs
    var lastMode: String
    var endpoint: String
        
    suspend fun coldStart()
    suspend fun getAccount(): Account
    suspend fun putAccount(account: Account)
    /* Not used
    suspend fun createAccount(account: Account)
    */
    suspend fun login(email: String, password: String): Account
    fun logout()
    
    fun changeEndpoint()
    fun getEndpoins(): ArrayList<String>
}
