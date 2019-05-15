package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.*

interface SessionRepository {

    val endpoint: Endpoint

    val isInitialized: Boolean
    val configs: Configs
    val account: Account
    var accessToken: String
    var userEmail: String
    var userPassword: String
    val mobileConfig: MobileConfig

    var favoriteTransportTypes: Set<TransportType.ID>?

    suspend fun coldStart(): Result<Account>
    suspend fun putAccount(account: Account, pass: String? = null, repeatedPass: String? = null): Result<Account>
    suspend fun putNoAccount(account: Account): Result<Account>
    suspend fun login(email: String, password: String): Result<Account>
    suspend fun accountLogin(email: String?, phone: String?, password: String): Result<Account>
    suspend fun getVerificationCode(email: String?, phone: String?): Result<Boolean>
    suspend fun logout(): Result<Account>
}