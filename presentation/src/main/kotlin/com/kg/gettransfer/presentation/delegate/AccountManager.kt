package com.kg.gettransfer.presentation.delegate

import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.interactor.SocketInteractor

import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.CreateOrderView.FieldError

import org.koin.core.KoinComponent
import org.koin.core.get

class AccountManager : KoinComponent {
    val sessionInteractor: SessionInteractor = get()
    val socketInteractor: SocketInteractor = get()

    /* REMOTE ACCOUNT */

    val remoteAccount: Account
        get() = sessionInteractor.account

    val remoteUser: User
        get() = remoteAccount.user

    val remoteProfile: Profile
        get() = remoteUser.profile

    val isLoggedIn: Boolean //is authorized user
        get() = !remoteProfile.email.isNullOrEmpty() || !remoteProfile.phone.isNullOrEmpty()

    val hasAccount: Boolean //is temporary or authorized user
        get() = isLoggedIn || (remoteProfile.email.isNullOrEmpty() && remoteProfile.phone.isNullOrEmpty() && remoteUser.termsAccepted)

    val hasData: Boolean
        get() = !remoteProfile.email.isNullOrEmpty() && !remoteProfile.phone.isNullOrEmpty()

    fun clearRemoteUser() {
        remoteUser.profile = Profile.EMPTY.copy()
        remoteUser.termsAccepted = false
    }

    /* TEMPORARY USER */

    val tempUser: User
        get() = sessionInteractor.tempUser

    val tempProfile: Profile
        get() = tempUser.profile

    fun initTempUser(user: User? = null) {
        val settedUser = user ?: remoteUser
        tempUser.profile = settedUser.profile.copy()
        tempUser.termsAccepted = settedUser.termsAccepted
    }

    fun isValidProfileForCreateOrder() =
        when {
            !tempUser.profile.email.isNullOrEmpty() && !Utils.checkEmail(tempUser.profile.email) -> FieldError.INVALID_EMAIL
            !tempUser.profile.phone.isNullOrEmpty() && !Utils.checkPhone(tempUser.profile.phone) -> FieldError.INVALID_PHONE
            !tempUser.termsAccepted -> FieldError.TERMS_ACCEPTED_FIELD
            else -> null
        }

    fun isValidEmailAndPhoneFieldsForPay() =
        when {
            tempUser.profile.email.isNullOrEmpty() -> FieldError.EMAIL_FIELD
            tempUser.profile.phone.isNullOrEmpty() -> FieldError.PHONE_FIELD
            !Utils.checkEmail(tempUser.profile.email) -> FieldError.INVALID_EMAIL
            !Utils.checkPhone(tempUser.profile.phone) -> FieldError.INVALID_PHONE
            else -> null
        }

    /* METHODS */

    suspend fun login(email: String?, phone: String?, password: String, withSmsCode: Boolean): Result<Account> {
        val result = sessionInteractor.login(email, phone, password, withSmsCode)
        if (result.error == null) {
            initTempUser(result.model.user)
            socketInteractor.openSocketConnection()
        }
        return result
    }

    suspend fun register(registerAccount: RegistrationAccount): Result<Account> {
        val result = sessionInteractor.register(registerAccount)
        if (result.error == null) initTempUser(result.model.user)
        return result
    }

    suspend fun logout(): Result<Account> {
        tempUser.profile = Profile.EMPTY.copy()
        socketInteractor.closeSocketConnection()
        return sessionInteractor.logout()
    }

    suspend fun putAccount(isTempAccount: Boolean = true, connectSocket: Boolean = false): Result<Account> {
        val result =
            sessionInteractor.putAccount(if (isTempAccount) remoteAccount.copy(user = tempUser) else remoteAccount)
        if (result.error == null) {
            if (connectSocket && hasAccount) socketInteractor.openSocketConnection()
            if (isTempAccount) initTempUser(result.model.user.copy())
        }
        return result
    }

    suspend fun putNoAccount(): Result<Account> {
        return sessionInteractor.putAccount()
    }

    /**
     * Save general settings
     */
    suspend fun saveSettings(): Result<Account>  {
        return if (hasAccount)
            putAccount(isTempAccount = false)
        else putNoAccount()
    }
}
