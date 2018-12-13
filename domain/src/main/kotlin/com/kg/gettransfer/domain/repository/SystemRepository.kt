package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.SystemListener

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Result

interface SystemRepository {
    val isInitialized: Boolean
    val configs: Configs
    val account: Account
    val accessToken: String
    val endpoints: List<Endpoint>

    var lastMode: String
    var isFirstLaunch: Boolean
    var isOnboardingShowed: Boolean
    var selectedField: String
    var endpoint: Endpoint
    var addressHistory: List<GTAddress>
    var appEnters: Int

    suspend fun coldStart(): Result<Account>
    suspend fun putAccount(account: Account): Result<Account>
    suspend fun login(email: String, password: String): Result<Account>
    fun logout(): Result<Account>

    fun addListener(listener: SystemListener)
    fun removeListener(listener: SystemListener)
}
