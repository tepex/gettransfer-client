package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.eventListeners.AccountChangedListener
import com.kg.gettransfer.domain.eventListeners.CreateTransferListener
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.RegistrationAccount
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.User

@Suppress("TooManyFunctions")
interface SessionRepository {

    val isInitialized: Boolean
    val account: Account
    val tempUser: User
    var userEmail: String?
    var userPhone: String?
    var userPassword: String
    var appLanguage: String
    var isAppLanguageChanged: Boolean

    suspend fun coldStart(): Result<Account>
    suspend fun updateOldToken(authKey: String?): Result<Unit>
    suspend fun putAccount(newAccount: Account, pass: String? = null, repeatedPass: String? = null): Result<Account>
    suspend fun putNoAccount(newAccount: Account): Result<Account>
    suspend fun login(email: String?, phone: String?, password: String, withSmsCode: Boolean): Result<Account>
    suspend fun register(registerAccount: RegistrationAccount): Result<Account>
    suspend fun getVerificationCode(email: String?, phone: String?): Result<Boolean>
    suspend fun logout(): Result<Account>

    suspend fun getConfirmationCode(email: String?, phone: String?): Result<Boolean>
    suspend fun changeContact(email: String?, phone: String?, code: String): Result<Boolean>

    fun addAccountChangedListener(listener: AccountChangedListener)
    fun removeAccountChangedListener(listener: AccountChangedListener)

    fun addCreateTransferListener(listener: CreateTransferListener)
    fun removeCreateTransferListener(listener: CreateTransferListener)
    fun notifyCreateTransfer()
}
