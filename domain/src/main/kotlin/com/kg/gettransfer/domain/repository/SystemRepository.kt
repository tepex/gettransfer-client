package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.SystemListener

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress

interface SystemRepository {
    val configs: Configs
    val account: Account
    val accessToken: String
    val endpoints: List<Endpoint>
    
    var lastMode: String
    var selectedField: String
    var endpoint: Endpoint
    var addressHistory: List<GTAddress>    
        
    suspend fun coldStart()
    suspend fun putAccount(account: Account)
    suspend fun login(email: String, password: String): Account
    fun logout()
    
    fun addListener(listener: SystemListener)
    fun removeListener(listener: SystemListener)
}
