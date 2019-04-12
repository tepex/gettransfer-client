package com.kg.gettransfer.presentation.presenter

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.Account.Companion.GROUP_CARRIER_DRIVER
import com.kg.gettransfer.domain.model.Account.Companion.GROUP_MANAGER_VIEW_TRANSFERS

import com.kg.gettransfer.presentation.ui.LoginActivity

import com.kg.gettransfer.presentation.view.LoginView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class LoginPresenter : BasePresenter<LoginView>() {
    private var password: String? = null

    internal var email: String? = null
    internal var screenForReturn: String? = null
    internal var transferId: Long = 0
    internal var offerId: Long? = null
    internal var rate: Int? = null

    fun onLoginClick() {
        if (!checkFields()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) { systemInteractor.login(email!!, password!!) }
                    .also {
                        it.error?.let { e ->
                            if (e.isNoUser())
                                viewState.showError(true, e.message)
                            logLoginEvent(Analytics.RESULT_FAIL)
                        }

                        it.isSuccess()?.let {
                            if (!screenForReturn.isNullOrEmpty()) openPreviousScreen(screenForReturn)
                            logLoginEvent(Analytics.RESULT_SUCCESS)
                            registerPushToken()
                        }
                    }
            viewState.blockInterface(false)
        }
    }

    private fun openPreviousScreen(screenForReturn: String?) {
        when (screenForReturn) {
            Screens.CARRIER_MODE   -> {
                router.navigateTo(Screens.ChangeMode(checkCarrierMode()))
            }
            Screens.PASSENGER_MODE -> {
                router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                analytics.logProfile(Analytics.PASSENGER_TYPE)
            }
            Screens.OFFERS         -> {
                router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                router.navigateTo(Screens.Offers(transferId))
            }
            Screens.CLOSE_AFTER_LOGIN -> router.exit()
            Screens.PAYMENT_OFFER -> {
                utils.launchSuspend {
                    fetchData (NO_CACHE_CHECK) { transferInteractor.getTransfer(transferId) }
                            ?.let { transfer ->
                                val transferModel = transferMapper.toView(transfer)

                                router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                                router.navigateTo(Screens.PaymentOffer(transferId, offerId, transferModel.dateRefund,
                                        transferModel.paymentPercentages!!, null))
                            }
                }
            }
            Screens.RATE_TRANSFER -> router.navigateTo(Screens.Splash(transferId, rate, true))
        }
    }

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    fun onHomeClick() = router.exit()

    fun setEmail(email: String) {
        this.email = if (email.isEmpty()) null else email
        enableLoginButton()
    }

    fun setPassword(password: String) {
        this.password = if (password.isEmpty()) null else password
        enableLoginButton()
    }

    private fun enableLoginButton() =
        viewState.enableBtnLogin(!isEmptyFields())

    private fun isEmptyFields(): Boolean {
        return email == null || password == null
    }

    private fun checkFields(): Boolean {
        val checkEmail = email != null && Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
        val checkPassword = password != null
        var fieldsValid = true
        var errorType = 0
        if (!checkEmail)         { fieldsValid = false; errorType = LoginActivity.INVALID_EMAIL }
        else if (!checkPassword) { fieldsValid = false; errorType = LoginActivity.INVALID_PASSWORD }
        viewState.showValidationError(!fieldsValid, errorType)
        return fieldsValid
    }

    private fun checkCarrierMode(): String {
        val groups = systemInteractor.account.groups
        if (groups.indexOf(GROUP_CARRIER_DRIVER) >= 0) {
            if (groups.indexOf(GROUP_MANAGER_VIEW_TRANSFERS) >= 0) analytics.logProfile(Analytics.CARRIER_TYPE)
            else analytics.logProfile(Analytics.DRIVER_TYPE)
            return Screens.CARRIER_MODE
        }
        return Screens.REG_CARRIER
    }

    fun onPassForgot() = router.navigateTo(Screens.RestorePassword)
}
