package com.kg.gettransfer.domain.repository

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

    var favoriteTransportTypes: Set<TransportType.ID>?
    var lastMode: String
    var lastMainScreenMode: String
    var lastCarrierTripsTypeView: String
    var firstDayOfWeek: Int
    var isFirstLaunch: Boolean
    var isOnboardingShowed: Boolean
    var selectedField: String
    var endpoint: Endpoint
    var addressHistory: List<GTAddress>
    var appEnters: Int

    suspend fun coldStart(): Result<Account>
    suspend fun putAccount(account: Account, pass: String? = null, repeatedPass: String? = null): Result<Account>
    suspend fun putNoAccount(account: Account): Result<Account>
    suspend fun login(email: String, password: String): Result<Account>
    suspend fun accountLogin(email: String?, phone: String?, password: String): Result<Account>
    suspend fun getVerificationCode(email: String?, phone: String?): Result<Boolean>
    suspend fun logout(): Result<Account>
}
