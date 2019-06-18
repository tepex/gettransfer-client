package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.*

interface SessionRepository {

    val endpoint: Endpoint

    val isInitialized: Boolean
    val configs: Configs
    val account: Account
    val tempUser: User
    var accessToken: String
    var userEmail: String?
    var userPhone: String?
    var userPassword: String
    val mobileConfig: MobileConfig

    var favoriteTransportTypes: Set<TransportType.ID>?

    suspend fun coldStart(): Result<Account>
    suspend fun putAccount(account: Account, pass: String? = null, repeatedPass: String? = null): Result<Account>
    suspend fun putNoAccount(account: Account): Result<Account>
    suspend fun login(email: String?, phone: String?, password: String, withSmsCode: Boolean): Result<Account>
    suspend fun register(registerAccount: RegistrationAccount): Result<Account>
    suspend fun getVerificationCode(email: String?, phone: String?): Result<Boolean>
    suspend fun logout(): Result<Account>

    suspend fun getCodeForChangeEmail(email: String): Result<Boolean>
    suspend fun changeEmail(email: String, code: String): Result<Boolean>
}