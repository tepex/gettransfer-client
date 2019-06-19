package com.kg.gettransfer.presentation.delegate

import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.CreateOrderView.FieldError

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class AccountManager : KoinComponent {
    val sessionInteractor: SessionInteractor = get()

    /* REMOTE ACCOUNT */

    val remoteAccount: Account
        get() = sessionInteractor.account

    val remoteUser: User
        get() = remoteAccount.user
        
    val remoteProfile: Profile
        get() = remoteUser.profile

    val isLoggedIn: Boolean
        get() = remoteUser !== User.EMPTY
        
    val hasAccount: Boolean
        get() = remoteUser !== User.EMPTY
        
    val hasData: Boolean
        get() = remoteProfile !== Profile.EMPTY

    fun clearRemoteUser() {
        remoteUser.profile = Profile.EMPTY
        remoteUser.termsAccepted = false
    }

    /* TEMPORARY USER */

    val tempUser: User
        get() = sessionInteractor.tempUser
        
    val tempProfile: Profile
        get() = tempUser.profile

    fun setTermsAccepted(isAccepted: Boolean) {
        tempUser.termsAccepted = isAccepted
    }
    
    fun initTempUser(user: User? = null) {
        val settedUser = user ?: remoteUser
        tempUser.profile.apply {
            fullName = settedUser.profile.fullName
            email = settedUser.profile.email
            phone = settedUser.profile.phone
        }
        tempUser.termsAccepted = settedUser.termsAccepted
    }

    private fun overwriteField(newField: String, tempField: String?, saveTempUserData: Boolean) =
        newField.isNotEmpty() && (tempField.isNullOrEmpty() || !saveTempUserData)

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
        if (result.error == null) initTempUser(result.model.user)
        return result
    }

    suspend fun register(registerAccount: RegistrationAccount): Result<Account> {
        val result = sessionInteractor.register(registerAccount)
        if (result.error == null) initTempUser(result.model.user)
        return result
    }

    suspend fun logout(): Result<Account> {
        tempUser.profile = Profile.EMPTY
        return sessionInteractor.logout()
    }

    suspend fun putAccount(isTempAccount: Boolean = false, updateTempUser: Boolean): Result<Account> {
        val result =
            sessionInteractor.putAccount(if (isTempAccount) remoteAccount.copy(user = tempUser) else remoteAccount)
        if (result.error == null && updateTempUser) initTempUser(result.model.user)
        return result
    }
}