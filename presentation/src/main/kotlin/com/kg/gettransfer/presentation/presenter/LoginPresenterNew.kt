package com.kg.gettransfer.presentation.presenter

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.Account.Companion.GROUP_CARRIER_DRIVER
import com.kg.gettransfer.domain.model.Account.Companion.GROUP_MANAGER_VIEW_TRANSFERS
import com.kg.gettransfer.extensions.firstSign

import com.kg.gettransfer.presentation.ui.LoginActivityNew

import com.kg.gettransfer.presentation.view.LoginViewNew
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import java.lang.IllegalArgumentException

@InjectViewState
class LoginPresenterNew : BasePresenter<LoginViewNew>() {
    internal var passwordFragmentIsShowing = false

    internal var email: String? = null
    internal var screenForReturn: String? = null
    internal var transferId: Long = 0
    internal var offerId: Long? = null
    internal var rate: Int? = null

    private var password: String? = null

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

    private fun checkIsNumber(): Boolean {
        if (email?.firstSign() == "+") return true
        return try {
            email?.toInt()
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun identifyLoginType(input: String) =
            input.contains("@")

    private fun formatPhone() =
            email?.let {
                when {
                    it.firstSign() == "8" ->
                        buildString {
                            "+7" + it.removeRange(0, 1)
                    }
                    it.firstSign() == "7" -> "+$it"
                    else                  -> it
            }

        }

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    fun onBackClick() = router.exit()

    fun setEmail(email: String) {
        this.email = if (email.isEmpty()) null else email
    }

    fun setPassword(password: String) {
        this.password = if (password.isEmpty()) null else password
    }

    fun onPassForgot() = router.navigateTo(Screens.RestorePassword)

    private fun checkFields(): Boolean {
        val checkEmail = email != null && Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
        val checkPassword = password != null
        var fieldsValid = true
        var errorType = 0
        if (!checkEmail)         { fieldsValid = false; errorType = LoginActivityNew.INVALID_EMAIL }
        else if (!checkPassword) { fieldsValid = false; errorType = LoginActivityNew.INVALID_PASSWORD }
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
}
