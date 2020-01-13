package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.AccountChangedListener
import com.kg.gettransfer.domain.eventListeners.CreateTransferListener
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.domain.model.RegistrationAccount
import com.kg.gettransfer.domain.model.User

import com.kg.gettransfer.domain.repository.GeoRepository
import com.kg.gettransfer.domain.repository.SessionRepository

import java.util.Locale

@Suppress("TooManyFunctions")
class SessionInteractor(
    private val sessionRepository: SessionRepository,
    private val geoRepository: GeoRepository
) {

    val isInitialized: Boolean
        get() = sessionRepository.isInitialized

    val account: Account
        get() = sessionRepository.account

    val tempUser: User
        get() = sessionRepository.tempUser

    // if account is empty  we need to use locale from preferences
    // because locale from Account.EMPTY is equals Locale.getDefault()
    // and then it'll be always show wrong locale
    var locale: Locale
        get() = if (account.user == User.EMPTY) Locale(appLanguage, Account.EMPTY.locale.country) else account.locale
        set(value) {
            account.locale = value
            appLanguage = value.language
            geoRepository.initGeocoder(value)
        }

    var currency: Currency
//        get() = if (systemInteractor.currencies.contains(account.currency)) account.currency else Currency.DEFAULT
        get() = account.currency
        set(value) {
            account.currency = value
        }

    var distanceUnit: DistanceUnit
        get() = account.distanceUnit
        set(value) {
            account.distanceUnit = value
        }

    var isEmailNotificationEnabled: Boolean
        get() = account.isEmailNotificationsEnabled
        set(value) {
            account.isEmailNotificationsEnabled = value
        }

    var appLanguage: String
        get() = sessionRepository.appLanguage
        set(value) {
            sessionRepository.appLanguage = value
            isAppLanguageChanged = true
        }

    var isAppLanguageChanged: Boolean
        get() = sessionRepository.isAppLanguageChanged
        set(value) { sessionRepository.isAppLanguageChanged = value }

    suspend fun coldStart() = sessionRepository.coldStart()

    suspend fun updateOldToken() = sessionRepository.updateOldToken()

    suspend fun logout() = sessionRepository.logout()
    suspend fun login(email: String?, phone: String?, password: String, withSmsCode: Boolean) =
        sessionRepository.login(email, phone, password, withSmsCode)

    suspend fun register(registrationAccount: RegistrationAccount) =
        sessionRepository.register(registrationAccount)

    suspend fun getVerificationCode(email: String?, phone: String?) =
        sessionRepository.getVerificationCode(email, phone)

    suspend fun putAccount(account: Account? = null) = sessionRepository.putAccount(account ?: this.account)
    suspend fun putNoAccount() = sessionRepository.putNoAccount(account)
    suspend fun changePassword(pass: String, repeatedPass: String) =
        sessionRepository.putAccount(account, pass, repeatedPass)

    suspend fun getCodeForChangeEmail(email: String) = sessionRepository.getCodeForChangeEmail(email)
    suspend fun changeEmail(email: String, code: String) = sessionRepository.changeEmail(email, code)

    fun addAccountChangedListener(listener: AccountChangedListener) {
        sessionRepository.addAccountChangedListener(listener)
    }

    fun removeAccountChangedListener(listener: AccountChangedListener) {
        sessionRepository.removeAccountChangedListener(listener)
    }

    fun addCreateTransferListener(listener: CreateTransferListener) {
        sessionRepository.addCreateTransferListener(listener)
    }

    fun removeCreateTransferListener(listener: CreateTransferListener) {
        sessionRepository.removeCreateTransferListener(listener)
    }

    fun notifyCreateTransfer() {
        sessionRepository.notifyCreateTransfer()
    }
}
