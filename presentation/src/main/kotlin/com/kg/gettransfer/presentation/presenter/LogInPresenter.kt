package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.extensions.internationalExample
import com.kg.gettransfer.presentation.ui.MainLoginActivity
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.CREDENTIALS_VALID
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_EMAIL
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_PHONE
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import org.koin.standalone.KoinComponent

@InjectViewState
class LogInPresenter : LogInBasePresenter<LogInView>(), KoinComponent {

    var password: String = ""

    val isEnabledButtonLogin
        get() = password.isNotEmpty() && params.emailOrPhone.isNotEmpty()
    val isEnabledRequestCodeButton
        get() = params.emailOrPhone.isNotEmpty()

    private fun isPhone(value: String = params.emailOrPhone) = LoginHelper.checkIsNumber(value)

    override fun attachView(view: LogInView) {
        super.attachView(view)
        if (params.emailOrPhone.isNotEmpty()) viewState.setEmail(params.emailOrPhone)
    }

    fun setEmailOrPhone(value: String) {
        params.emailOrPhone = if (isPhone(value)) LoginHelper.formatPhone(value) else value
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

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    override fun onBackCommandClick() {
        router.backTo(Screens.MainPassenger())
    }

    private fun loginWithCode() {
        if (!checkInputData()) return
        viewState.hideLoading()
        val isPhone = isPhone()
        router.replaceScreen(
            Screens.SmsCode(
                params,
                isPhone
            )
        )
    }

    fun onLoginClick() {
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
                    true -> accountManager.login(null, LoginHelper.formatPhone(params.emailOrPhone), password, false)
                    false -> accountManager.login(params.emailOrPhone, null, password, false)
                }
            }.also {
                it.error?.let { e ->
                    viewState.setError(e)
                    logLoginEvent(Analytics.RESULT_FAIL)
                }

                it.isSuccess()?.let {
                    openNextScreen()
                    logLoginEvent(Analytics.RESULT_SUCCESS)
                    registerPushToken()
                    router.exit()
                }

                viewState.hideLoading()
            }
        }
    }

    fun sendVerificationCode() {
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
                    true -> sessionInteractor.getVerificationCode(null, LoginHelper.formatPhone(params.emailOrPhone))
                    false -> sessionInteractor.getVerificationCode(params.emailOrPhone, null)
                }
            }.also {
                if (it.error != null)
                    viewState.setError(it.error!!)
                else {
                    loginWithCode()
                }
            }
        }
    }

    fun getPhoneExample(): String = Utils.phoneUtil.internationalExample(sessionInteractor.locale)

}
