package com.kg.gettransfer.presentation.presenter

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Account

import com.kg.gettransfer.presentation.ui.LoginActivity

import com.kg.gettransfer.presentation.view.LoginView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import com.yandex.metrica.YandexMetrica

@InjectViewState
class LoginPresenter: BasePresenter<LoginView>() {
    companion object {
        @JvmField val MIN_PASSWORD_LENGTH = 6

        @JvmField val RESULT_CODE = 33
        @JvmField val RESULT_OK   = 1
    }

    private var password: String? = null

    internal var email: String? = null
    internal var screenForReturn: String? = null
    internal var transferId: Long? = null

    fun onLoginClick() {
        if(!checkFields()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { systemInteractor.login(email!!, password!!) }
            if(result.error == null) {
                if(!screenForReturn.isNullOrEmpty()) {
                    when(screenForReturn) {
                        Screens.CARRIER_MODE   -> router.navigateTo(Screens.ChangeMode(checkCarrierMode()))
                        Screens.PASSENGER_MODE -> router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                        Screens.OFFERS         -> {
                            if(transferId != 0L) router.navigateTo(Screens.Offers(transferId!!))
                            else router.exit()
                        }
                    }
                }
                else router.exit()
                //else router.exitWithResult(RESULT_CODE, RESULT_OK)
                logLoginEvent(Analytics.RESULT_SUCCESS)
            } else {
                viewState.showError(true, result.error!!.message)
                logLoginEvent(Analytics.RESULT_FAIL)
            }
            viewState.blockInterface(false)
        }
    }

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    fun onHomeClick() = router.exit()

    fun setEmail(email: String) { this.email = if(email.isEmpty()) null else email }
    fun setPassword(password: String) { this.password = if(password.isEmpty()) null else password }

    private fun checkFields(): Boolean {
        val checkEmail = email != null && Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
        val checkPassword = password != null
        var fieldsValid = true
        var errorType = 0
        if(!checkEmail)         { fieldsValid = false; errorType = LoginActivity.INVALID_EMAIL }
        else if(!checkPassword) { fieldsValid = false; errorType = LoginActivity.INVALID_PASSWORD }
        viewState.showValidationError(!fieldsValid, errorType)
        return fieldsValid
    }

    private fun checkCarrierMode() =
        if(systemInteractor.account.groups.indexOf(Account.GROUP_CARRIER_DRIVER) >= 0) Screens.CARRIER_MODE
        else Screens.REG_CARRIER

    fun onPassForgot() = router.navigateTo(Screens.RestorePassword)
}
