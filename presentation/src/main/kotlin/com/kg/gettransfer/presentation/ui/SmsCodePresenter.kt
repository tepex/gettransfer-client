package com.kg.gettransfer.presentation.ui

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.firstSign
import com.kg.gettransfer.extensions.internationalExample
import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.ui.MainLoginActivity.Companion.INVALID_PHONE
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.CREDENTIALS_VALID
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_EMAIL
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SmsCodeView
import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class SmsCodePresenter : BasePresenter<SmsCodeView>() {

    val smsResendDelaySec = sessionInteractor.mobileConfigs.smsResendDelaySec

    internal var nextScreen: String? = null
    internal var emailOrPhone: String? = null

    fun sendVerificationCode(emailOrPhone: String, isPhone: Boolean) {
        if (!checkInputData(emailOrPhone, isPhone)) return

        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> sessionInteractor.getVerificationCode(null, LoginHelper.formatPhone(emailOrPhone))
                    false -> sessionInteractor.getVerificationCode(emailOrPhone, null)
                }
            }.also {
                if (it.error != null)
                    viewState.setError(it.error!!)
                else {
                    viewState.updateTimerResendCode()
                }
            }
        }
    }

    private fun checkInputData(emailOrPhone: String, isPhone: Boolean) =
        checkIfEmailOrPhone(emailOrPhone) && validateInput(emailOrPhone, isPhone)

    private fun checkIfEmailOrPhone(emailOrPhone: String) = emailOrPhone.run {
        if (firstSign() == PHONE_ATTRIBUTE || any { it.toString() == EMAIL_ATTRIBUTE }) {
            true
        } else {
            viewState.setError(
                false,
                R.string.LNG_ERROR_EMAIL_PHONE,
                Utils.phoneUtil.internationalExample(sessionInteractor.locale)
            )
            false
        }
    }

    private fun validateInput(emailOrPhone: String, isPhone: Boolean): Boolean {
        LoginHelper.validateInput(emailOrPhone, isPhone) //force unwrap because null-check is already done
            .also {
                when (it) {
                    INVALID_EMAIL -> viewState.showValidationError(true, INVALID_EMAIL)
                    INVALID_PHONE -> viewState.showValidationError(true, INVALID_PHONE)
                    CREDENTIALS_VALID -> return true
                }
            }
        return false
    }

    fun onLoginClick(emailOrPhone: String, password: String, isPhone: Boolean) {
        if (!checkInputData(emailOrPhone, isPhone)) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> accountManager.login(null, LoginHelper.formatPhone(emailOrPhone), password, true)
                    false -> accountManager.login(emailOrPhone, null, password, true)
                }
            }
                .also {
                    it.error?.let { e ->
                        viewState.setError(e)
                        logLoginEvent(Analytics.RESULT_FAIL)
                    }

                    it.isSuccess()?.let {
                        viewState.showErrorText(false)
                        viewState.showChangePasswordDialog()
                        logLoginEvent(Analytics.RESULT_SUCCESS)
                        registerPushToken()
                    }
                }
            viewState.blockInterface(false)
        }
    }

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    fun back() {
        router.replaceScreen(Screens.AuthorizationPager(nextScreen ?: Screens.CLOSE_AFTER_LOGIN, emailOrPhone))
    }

    companion object {
        const val PHONE_ATTRIBUTE = "+"
        const val EMAIL_ATTRIBUTE = "@"
    }
}
