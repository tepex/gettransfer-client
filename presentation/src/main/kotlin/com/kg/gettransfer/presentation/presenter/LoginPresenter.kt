package com.kg.gettransfer.presentation.presenter

import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.view.LoginView

import com.yandex.metrica.YandexMetrica

import org.koin.standalone.inject

@InjectViewState
class LoginPresenter: BasePresenter<LoginView>() {
    companion object {
        @JvmField val MIN_PASSWORD_LENGTH = 6
        
        @JvmField val RESULT_CODE = 33
        @JvmField val RESULT_OK   = 1

        @JvmField val EVENT = "login"
        @JvmField val PARAM_KEY = "status"
    }

    private var email: String? = null
    private var password: String? = null

    var screenForReturn: String? = null

    fun onLoginClick() {
        if(!checkFields()) return
        
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { systemInteractor.login(email!!, password!!) }
            if(result.error == null) {
                if(!screenForReturn.isNullOrEmpty()) {
                    when (screenForReturn) {
                        Screens.CARRIER_MODE   -> router.navigateTo(checkCarrierMode())
                        Screens.OFFERS         -> router.navigateTo(Screens.OFFERS)
                        Screens.CLOSE_ACTIVITY -> router.exit()
                    }
                } else router.exitWithResult(RESULT_CODE, RESULT_OK)
                logLoginEvent(RESULT_SUCCESS)
            } else {
                viewState.setError(result.error!!)
                logLoginEvent(RESULT_FAIL)
            }
            viewState.blockInterface(false)
        }
    }

    private fun logLoginEvent(result: String) {
        val map = HashMap<String, Any>()
        map[PARAM_KEY] = result

        mFBA.logEvent(EVENT, createSingeBundle(PARAM_KEY, result))
        eventsLogger.logEvent(EVENT, createSingeBundle(PARAM_KEY, result))
        YandexMetrica.reportEvent(EVENT, map)
    }

    fun onHomeClick() = router.exit()

    fun setEmail(email: String) { this.email = if (email.isEmpty()) null else email }

    fun setPassword(password: String) { this.password = if (password.isEmpty()) null else password }

    private fun checkFields(): Boolean {
        val checkEmail = email != null && Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
        val checkPassword = password != null && password!!.length >= MIN_PASSWORD_LENGTH
        viewState.showError(!(checkEmail && checkPassword))
        return checkEmail && checkPassword
    }

    private fun checkCarrierMode(): String {
        if (systemInteractor.account.groups.indexOf(Account.GROUP_CARRIER_DRIVER) >= 0)
            return Screens.CARRIER_MODE
        else return Screens.REG_CARRIER
    }
}
