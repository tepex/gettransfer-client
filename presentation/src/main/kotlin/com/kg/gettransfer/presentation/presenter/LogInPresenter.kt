package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.extensions.internationalExample

import com.kg.gettransfer.presentation.ui.MainLoginActivity
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.CREDENTIALS_VALID
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_EMAIL
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_PHONE
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.getInternationalNumber

import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.KoinComponent

@InjectViewState
class LogInPresenter : OpenNextScreenPresenter<LogInView>(), KoinComponent {

    var password: String = ""

    val isEnabledButtonLogin
        get() = password.isNotEmpty() && params.emailOrPhone.isNotEmpty()
    val isEnabledRequestCodeButton
        get() = params.emailOrPhone.isNotEmpty()

    fun isPhone(value: String = params.emailOrPhone) = LoginHelper.checkIsNumber(value)

    override fun attachView(view: LogInView) {
        super.attachView(view)
        if (params.emailOrPhone.isNotEmpty()) viewState.setEmail(params.emailOrPhone)
    }

    fun setEmailOrPhone(value: String) {
        params.emailOrPhone = value.trim()
    }

    private fun validateInput(): Boolean {
        LoginHelper.validateInput(
            params.emailOrPhone,
            isPhone()
        ).also {
            when (it) {
                INVALID_EMAIL -> viewState.showValidationError(INVALID_EMAIL)
                INVALID_PHONE -> viewState.showValidationError(INVALID_PHONE)
                CREDENTIALS_VALID -> return true
            }
        }
        return false
    }

    private fun checkInputData() = params.emailOrPhone.isNotEmpty() && validateInput()

    override fun onBackCommandClick() {
        router.backTo(Screens.MainPassenger())
    }

    private fun loginWithCode() {
        if (!checkInputData()) return
        viewState.hideLoading()
        router.replaceScreen(Screens.SmsCode(params, isPhone()))
    }

    fun onLoginClick() {
        analytics.logSingleEvent(Analytics.VERIFY_PASSWORD_CLICKED)
        if (password.isEmpty()) {
            viewState.showValidationError(MainLoginActivity.INVALID_PASSWORD)
            return
        }
        if (params.emailOrPhone.isEmpty() ||
            isPhone() && !LoginHelper.phoneIsValid(params.emailOrPhone) ||
            !isPhone() && !LoginHelper.emailIsValid(params.emailOrPhone)
        ) {
            viewState.showValidationError(MainLoginActivity.INVALID_EMAIL)
            return
        }

        if (!checkInputData()) return

        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone()) {
                    true -> accountManager.login(null, getInternationalNumber(params.emailOrPhone), password, false)
                    false -> accountManager.login(params.emailOrPhone, null, password, false)
                }
            }.also {
                it.error?.let { e ->
                    viewState.setError(e)
                    analytics.logEvent(Analytics.EVENT_LOGIN_PASS, Analytics.STATUS, Analytics.RESULT_FAIL)
                }

                it.isSuccess()?.let {
                    openNextScreen()
                    analytics.logEvent(Analytics.EVENT_LOGIN_PASS, Analytics.STATUS, Analytics.RESULT_SUCCESS)
                    registerPushToken()
                    router.exit()
                }

                viewState.hideLoading()
            }
        }
    }

    fun sendVerificationCode() {
        analytics.logSingleEvent(Analytics.GET_CODE_CLICKED)
        if (params.emailOrPhone.isEmpty() ||
            (!isPhone() && !LoginHelper.emailIsValid(params.emailOrPhone)) ||
            (isPhone() && !LoginHelper.phoneIsValid(params.emailOrPhone))
        ) {
            viewState.showValidationError(MainLoginActivity.INVALID_EMAIL)
            return
        }
        val isPhone = isPhone()
        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> {
                        params.emailOrPhone = getInternationalNumber(params.emailOrPhone)
                        sessionInteractor.getVerificationCode(null, params.emailOrPhone)
                    }
                    false -> sessionInteractor.getVerificationCode(params.emailOrPhone, null)
                }
            }.also {
                if (it.error != null) {
                    viewState.setError(it.error!!)
                    analytics.logEvent(Analytics.EVENT_GET_CODE, Analytics.STATUS, Analytics.RESULT_FAIL)
                } else {
                    loginWithCode()
                    analytics.logEvent(Analytics.EVENT_GET_CODE, Analytics.STATUS, Analytics.RESULT_SUCCESS)
                }
            }
        }
    }

    fun getPhoneExample(): String = Utils.phoneUtil.internationalExample(sessionInteractor.locale)
}
