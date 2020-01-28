package com.kg.gettransfer.presentation.delegate

import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.interactor.SocketInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.CountEventsInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.User
import com.kg.gettransfer.domain.model.RegistrationAccount
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.CreateOrderView.FieldError
import com.kg.gettransfer.sys.domain.ClearConfigsInteractor
import com.kg.gettransfer.sys.domain.GetConfigsInteractor
import com.kg.gettransfer.sys.domain.SetFavoriteTransportsInteractor

import org.koin.core.KoinComponent
import org.koin.core.get

class AccountManager : KoinComponent {
    private val sessionInteractor: SessionInteractor = get()
    private val socketInteractor: SocketInteractor = get()
    private val pushTokenManager: PushTokenManager = get()
    private val clearConfigsInteractor: ClearConfigsInteractor = get()
    private val getConfigsInteractor: GetConfigsInteractor = get()

    private val transferInteractor: TransferInteractor = get()
    private val offerInteractor: OfferInteractor = get()
    private val reviewInteractor: ReviewInteractor = get()
    private val orderInteractor: OrderInteractor = get()
    private val countEventsInteractor: CountEventsInteractor = get()
    private val setFavoriteTransports: SetFavoriteTransportsInteractor = get()

    /* REMOTE ACCOUNT */

    val remoteAccount: Account
        get() = sessionInteractor.account

    val remoteUser: User
        get() = remoteAccount.user

    val remoteProfile: Profile
        get() = remoteUser.profile

    val isLoggedIn: Boolean // is authorized user
        get() = !remoteProfile.email.isNullOrEmpty() || !remoteProfile.phone.isNullOrEmpty()

    val hasAccount: Boolean // is temporary or authorized user
        get() = isLoggedIn ||
            remoteProfile.email.isNullOrEmpty() && remoteProfile.phone.isNullOrEmpty() && remoteUser.termsAccepted

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
            !tempUser.profile.email.isNullOrEmpty() &&
                !Utils.checkEmail(tempUser.profile.email) -> FieldError.INVALID_EMAIL
            !tempUser.profile.phone.isNullOrEmpty() &&
                !Utils.checkPhone(tempUser.profile.phone) -> FieldError.INVALID_PHONE
            !tempUser.termsAccepted                       -> FieldError.TERMS_ACCEPTED_FIELD
            else                                          -> null
        }

    fun isValidEmailAndPhoneFieldsForPay() =
        when {
            tempUser.profile.email.isNullOrEmpty()    -> FieldError.EMAIL_FIELD
            tempUser.profile.phone.isNullOrEmpty()    -> FieldError.PHONE_FIELD
            !Utils.checkEmail(tempUser.profile.email) -> FieldError.INVALID_EMAIL
            !Utils.checkPhone(tempUser.profile.phone) -> FieldError.INVALID_PHONE
            else -> null
        }

    /* METHODS */

    suspend fun login(email: String?, phone: String?, password: String, withSmsCode: Boolean): Result<Account> {
        val result = sessionInteractor.login(email, phone, password, withSmsCode)
        if (result.error == null) successAuthorization(result.model.user)
        return result
    }

    suspend fun register(registerAccount: RegistrationAccount): Result<Account> {
        val result = sessionInteractor.register(registerAccount)
        if (result.error == null) successAuthorization(result.model.user)
        return result
    }

    private suspend fun successAuthorization(user: User) {
        initTempUser(user)
        socketInteractor.openSocketConnection()
        pushTokenManager.registerPushToken()
        updateConfigs()
    }

    suspend fun logout() =
        sessionInteractor.logout().also {
            if (!it.isError()) {
                tempUser.profile = Profile.EMPTY.copy()
                socketInteractor.closeSocketConnection()
                clearAccountData()
            }
        }

    private suspend fun clearAccountData() {
        if (remoteAccount.partner?.defaultPromoCode != null) orderInteractor.promoCode = ""
        transferInteractor.clearTransfersCache()
        offerInteractor.clearOffersCache()
        reviewInteractor.clearReviewCache()

        countEventsInteractor.clearCountEvents()

        orderInteractor.clear()
        setFavoriteTransports(emptySet())
    }

    suspend fun putAccount(
        isTempAccount: Boolean = true,
        connectSocket: Boolean = false,
        updateConfigs: Boolean = false
    ): Result<Account> {
        val result =
            sessionInteractor.putAccount(if (isTempAccount) remoteAccount.copy(user = tempUser) else remoteAccount)
        if (result.error == null) {
            if (connectSocket && hasAccount) socketInteractor.openSocketConnection()
            if (hasAccount) pushTokenManager.registerPushToken()
            if (isTempAccount) initTempUser(result.model.user.copy())
            if (updateConfigs) updateConfigs()
        }
        return result
    }

    private suspend fun putNoAccount(): Result<Account> {
        return sessionInteractor.putNoAccount()
    }

    private suspend fun updateConfigs() {
        clearConfigsInteractor()
        getConfigsInteractor().getModel()
    }

    /**
     * Save general settings
     */
    suspend fun saveSettings(): Result<Account>  {
        return if (hasAccount) {
            putAccount(isTempAccount = false)
        } else {
            putNoAccount()
        }
    }
}
