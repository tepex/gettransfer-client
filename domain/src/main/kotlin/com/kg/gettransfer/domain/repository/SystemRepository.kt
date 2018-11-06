package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.SystemListener

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress

interface SystemRepository {
    var configs: Configs?
    var lastMode: String
    var selectedField: String
    var endpoint: Endpoint
    val accessToken: String
    val endpoints: List<Endpoint>
    var isInternetAvailable: Boolean
        
    suspend fun coldStart()
    suspend fun getAccount(): Account
    suspend fun putAccount(account: Account)
    /* Not used
    suspend fun createAccount(account: Account)
    */
    suspend fun login(email: String, password: String): Account
    fun logout()
    fun getHistory(): List<GTAddress>
    fun setHistory(history: List<GTAddress>)
    
    fun addListener(listener: SystemListener)
    fun removeListener(listener: SystemListener)
}
