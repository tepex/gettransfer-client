package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.SystemListener
import com.kg.gettransfer.domain.eventListeners.SystemEventListener
import com.kg.gettransfer.domain.model.*

interface SystemRepository {
    val isInitialized: Boolean
    val configs: Configs
    val account: Account
    var accessToken: String
    var userEmail: String
    var userPassword: String
    val endpoints: List<Endpoint>
    val mobileConfig: MobileConfig

    var lastMode: String
    var lastCarrierTripsTypeView: String
    var isFirstLaunch: Boolean
    var isOnboardingShowed: Boolean
    var selectedField: String
    var endpoint: Endpoint
    var addressHistory: List<GTAddress>
    var appEnters: Int
    var eventsCount : Int
    var transferIds: List<Long>
    var region: Region

    suspend fun coldStart(): Result<Account>
    suspend fun putAccount(account: Account): Result<Account>
    suspend fun putNoAccount(account: Account): Result<Account>
    suspend fun login(email: String, password: String): Result<Account>
    suspend fun logout(): Result<Account>
    suspend fun registerPushToken(provider: PushTokenType, token: String): Result<Unit>
    suspend fun unregisterPushToken(token: String): Result<Unit>
    suspend fun getMyLocation(): Result<Location>

    fun connectSocket()
    fun disconnectSocket()
    fun connectionChanged()

    fun addListener(listener: SystemEventListener)
    fun removeListener(listener: SystemEventListener)
}
