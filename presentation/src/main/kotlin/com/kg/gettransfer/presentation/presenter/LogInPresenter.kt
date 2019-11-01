package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.extensions.internationalExample
import moxy.InjectViewState

import com.kg.gettransfer.presentation.ui.MainLoginActivity
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_EMAIL
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_PHONE
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.getInternationalNumber

import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.KoinComponent

@InjectViewState
class LogInPresenter : BaseLogInPresenter<LogInView>(), KoinComponent {

    var password: String = ""

    val isEnabledButtonLogin
        get() = password.isNotEmpty() && params.emailOrPhone.isNotEmpty()
    val isEnabledRequestCodeButton
        get() = params.emailOrPhone.isNotEmpty()
    val isPhone
        get() = LoginHelper.checkIsNumber(params.emailOrPhone)

    override fun attachView(view: LogInView) {
        super.attachView(view)
        if (params.emailOrPhone.isNotEmpty()) viewState.setEmail(params.emailOrPhone)
    }

    fun setEmailOrPhone(value: String) {
        params.emailOrPhone = value.trim()
    }

    fun onLoginClick() {
        if (!isCanLogIn()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> accountManager.login(null, getInternationalNumber(params.emailOrPhone), password, false)
                    false -> accountManager.login(params.emailOrPhone, null, password, false)
                }
            }.also { result ->
                viewState.blockInterface(false)

                result.error?.let { e ->
                    viewState.setError(e)
                    logEvent(Analytics.EVENT_LOGIN_PASS, Analytics.RESULT_FAIL)
                }

                result.isSuccess()?.let {
                    openNextScreen()
                    logEvent(Analytics.EVENT_LOGIN_PASS, Analytics.RESULT_SUCCESS)
                }
            }
        }
    }

    fun sendVerificationCode() {
        analytics.logSingleEvent(Analytics.GET_CODE_CLICKED)

        if (!validateInput()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> {
                        params.emailOrPhone = getInternationalNumber(params.emailOrPhone)
                        sessionInteractor.getVerificationCode(null, params.emailOrPhone)
                    }
                    false -> sessionInteractor.getVerificationCode(params.emailOrPhone, null)
                }
            }.also { result ->
                viewState.blockInterface(false)

                result.error?.let { e ->
                    viewState.setError(e)
                    logEvent(Analytics.EVENT_GET_CODE, Analytics.RESULT_FAIL)
                }

                result.isSuccess()?.let {
                    router.replaceScreen(Screens.SmsCode(params, isPhone))
                    logEvent(Analytics.EVENT_GET_CODE, Analytics.RESULT_SUCCESS)
                }
            }
        }
    }

    private fun isCanLogIn(): Boolean {
        analytics.logSingleEvent(Analytics.VERIFY_PASSWORD_CLICKED)

        return if (password.isEmpty()) {
            showValidationError(MainLoginActivity.INVALID_PASSWORD)
            false
        } else {
            validateInput()
        }
    }

    private fun validateInput() =
        if (params.emailOrPhone.isEmpty()) {
            showValidationError(MainLoginActivity.INVALID_EMAIL)
            false
        } else checkEmailOrPhone()

    private fun checkEmailOrPhone() =
        LoginHelper.validateInput(
            params.emailOrPhone,
            isPhone
        ).let { error ->
            if (error == INVALID_EMAIL || error == INVALID_PHONE) {
                showValidationError(error)
                false
            } else true
        }

    private fun showValidationError(errorType: Int) {
        viewState.showValidationError(errorType, Utils.phoneUtil.internationalExample(sessionInteractor.locale))
    }

    override fun onBackCommandClick() {
        router.backTo(Screens.MainPassenger())
    }
}
