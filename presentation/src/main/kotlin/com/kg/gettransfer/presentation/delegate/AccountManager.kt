package com.kg.gettransfer.presentation.delegate

import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.User
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.CreateOrderView.FieldError
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class AccountManager : KoinComponent {
    val sessionInteractor: SessionInteractor = get()
    val systemInteractor: SystemInteractor = get()
    val orderInteractor: OrderInteractor = get()

    /* REMOTE ACCOUNT */

    val remoteAccount: Account
        get() = sessionInteractor.account

    val remoteUser: User
        get() = remoteAccount.user

    val remoteProfile: Profile
        get() = remoteUser.profile

    val isLoggedIn: Boolean
        get() = remoteUser.loggedIn
    val hasAccount: Boolean
        get() = remoteUser.hasAccount

    fun clearRemoteUser() {
        remoteProfile.clear()
        remoteUser.termsAccepted = false
    }


    /* TEMPORARY USER */

    val tempUser: User
        get() = sessionInteractor.tempUser

    val tempProfile: Profile
        get() = tempUser.profile

    private fun initTempUser(user: User) {
        tempUser.profile.apply {
            fullName = user.profile.fullName
            email = user.profile.email
            phone = user.profile.phone
        }
        tempUser.termsAccepted = user.termsAccepted
    }

    private fun overwriteField(newField: String, tempField: String?, saveTempUserData: Boolean) =
            newField.isNotEmpty() && (tempField.isNullOrEmpty() || !saveTempUserData)

    fun isValidProfileForCreateOrder() =
            when {
                !tempProfile.email.isNullOrEmpty() && !Utils.checkEmail(tempProfile.email) -> FieldError.INVALID_EMAIL
                !tempProfile.phone.isNullOrEmpty() && !Utils.checkPhone(tempProfile.phone) -> FieldError.INVALID_PHONE
                !tempUser.termsAccepted -> FieldError.TERMS_ACCEPTED_FIELD
                else -> null
            }

    fun isValidEmailAndPhoneFieldsForPay() =
            when {
                tempProfile.email.isNullOrEmpty() -> FieldError.EMAIL_FIELD
                tempProfile.phone.isNullOrEmpty() -> FieldError.PHONE_FIELD
                !Utils.checkEmail(tempProfile.email) -> FieldError.INVALID_EMAIL
                !Utils.checkPhone(tempProfile.phone) -> FieldError.INVALID_PHONE
                else -> null
            }

    fun clearTempUser() {
        tempUser.profile.clear()
        tempUser.termsAccepted = false
    }


    /* METHODS */

    suspend fun login(email: String?, phone: String?, password: String, withSmsCode: Boolean): Result<Account> {
        val result = sessionInteractor.login(email, phone, password, withSmsCode)
        if (result.error == null) initTempUser(result.model.user)
        return result
    }

    suspend fun logout(): Result<Account> {
        clearTempUser()
        return sessionInteractor.logout()
    }

    suspend fun putAccount(isTempAccount: Boolean = false): Result<Account> {
        val result = sessionInteractor.putAccount(if (isTempAccount) remoteAccount.copy(user = tempUser) else remoteAccount )
        if (result.error == null) initTempUser(result.model.user)
        return result
    }
}