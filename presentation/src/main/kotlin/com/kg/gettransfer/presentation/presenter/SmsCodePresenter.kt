package com.kg.gettransfer.presentation.presenter

import android.os.CountDownTimer
import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.firstSign
import com.kg.gettransfer.extensions.internationalExample
import com.kg.gettransfer.presentation.ui.MainLoginActivity.Companion.INVALID_PHONE
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.CREDENTIALS_VALID
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_EMAIL
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SmsCodeView
import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class SmsCodePresenter : BasePresenter<SmsCodeView>() {

    var isPhone = false
    var pinCode = ""
    var emailOrPhone = ""
    var nextScreen: String? = null
    var pinItemsCount = PIN_ITEMS_COUNT
    val smsResendDelaySec = sessionInteractor.mobileConfigs.smsResendDelaySec * SEC_IN_MILLIS
    private lateinit var timerBtnResendCode: CountDownTimer

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        setTimer()
    }

    fun sendVerificationCode() {
        if (!checkInputData()) return

        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> sessionInteractor.getVerificationCode(null, LoginHelper.formatPhone(emailOrPhone))
                    false -> sessionInteractor.getVerificationCode(emailOrPhone, null)
                }
            }.also { result ->
                if (result.error != null) {
                    viewState.setError(result.error!!)
                } else {
                    viewState.startTimer()
                    timerBtnResendCode.start()
                }
            }
        }
    }

    private fun checkInputData() = checkIfEmailOrPhone() && validateInput()

    private fun checkIfEmailOrPhone() = emailOrPhone.run {
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

    private fun validateInput(): Boolean {
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

    fun onLoginClick() {
        if (!checkInputData()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> accountManager.login(null, LoginHelper.formatPhone(emailOrPhone), pinCode, true)
                    false -> accountManager.login(emailOrPhone, null, pinCode, true)
                }
            }.also {
                it.error?.let { e ->
                    viewState.setError(e)
                    logLoginEvent(Analytics.RESULT_FAIL)
                }

                it.isSuccess()?.let {
                    viewState.showErrorText(false)
                    logLoginEvent(Analytics.RESULT_SUCCESS)
                    registerPushToken()
                    router.exit()
                }
            }
            viewState.blockInterface(false)
        }
    }

    fun setTimer() {
        timerBtnResendCode = object : CountDownTimer(smsResendDelaySec, SEC_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                viewState.tickTimer(millisUntilFinished, SEC_IN_MILLIS)
            }

            override fun onFinish() {
                viewState.finishTimer()
            }
        }
        viewState.startTimer()
        timerBtnResendCode.start()
    }

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_LOGIN, createStringBundle(Analytics.STATUS, result), map)
    }

    fun back() {
        router.replaceScreen(Screens.AuthorizationPager(nextScreen ?: Screens.CLOSE_AFTER_LOGIN, emailOrPhone))
    }

    fun getTitleId(): Int = if (isPhone) R.string.LNG_LOGIN_SEND_SMS_CODE else R.string.LNG_LOGIN_SEND_EMAIL_CODE

    fun checkAfterPinChanged() {
        viewState.showErrorText(false)
        viewState.setBtnDoneIsEnabled(pinCode.length == pinItemsCount)
    }

    companion object {
        const val PHONE_ATTRIBUTE = "+"
        const val EMAIL_ATTRIBUTE = "@"
        const val SEC_IN_MILLIS = 1_000L
        const val PIN_ITEMS_COUNT = 4
    }
}
