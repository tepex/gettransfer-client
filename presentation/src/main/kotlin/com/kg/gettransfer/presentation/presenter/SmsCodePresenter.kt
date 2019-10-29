package com.kg.gettransfer.presentation.presenter

import android.os.CountDownTimer

import moxy.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.core.domain.Second

import com.kg.gettransfer.extensions.firstSign
import com.kg.gettransfer.extensions.internationalExample

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.CREDENTIALS_VALID

import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SmsCodeView

import com.kg.gettransfer.sys.presentation.ConfigsManager

import com.kg.gettransfer.utilities.Analytics

import kotlinx.serialization.json.JSON

import org.koin.core.get

@InjectViewState
class SmsCodePresenter : OpenNextScreenPresenter<SmsCodeView>() {

    private val smsResendDelay = get<ConfigsManager>().mobile.smsResendDelay.millis

    var isPhone = false
    var pinCode = ""
    var pinItemsCount = PIN_ITEMS_COUNT

    private val timerBtnResendCode: CountDownTimer = object : CountDownTimer(smsResendDelay, Second.MILLIS_PER_SECOND) {
        override fun onTick(millisUntilFinished: Long) {
            viewState.tickTimer(millisUntilFinished, Second.MILLIS_PER_SECOND)
        }

        override fun onFinish() {
            viewState.finishTimer()
        }
    }

    fun sendVerificationCode() {
        analytics.logSingleEvent(Analytics.RESEND_CODE_CLICKED)
        if (!checkInputData()) return

        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> sessionInteractor.getVerificationCode(null, params.emailOrPhone)
                    false -> sessionInteractor.getVerificationCode(params.emailOrPhone, null)
                }
            }.also { result ->
                if (result.error != null) {
                    result.error?.let { viewState.setError(it) }
                    logEvent(Analytics.EVENT_RESEND_CODE, Analytics.RESULT_FAIL)
                } else {
                    setTimer()
                    logEvent(Analytics.EVENT_RESEND_CODE, Analytics.RESULT_SUCCESS)
                }
            }
        }
    }

    private fun checkInputData() = checkIfEmailOrPhone() && validateInput()

    private fun checkIfEmailOrPhone() = params.emailOrPhone.run {
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

    private fun validateInput() =
        LoginHelper.validateInput(params.emailOrPhone, isPhone) == CREDENTIALS_VALID

    fun onLoginClick() {
        analytics.logSingleEvent(Analytics.VERIFY_CODE_CLICKED)
        if (!checkInputData()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> accountManager.login(null, params.emailOrPhone, pinCode, true)
                    false -> accountManager.login(params.emailOrPhone, null, pinCode, true)
                }
            }.also { result ->
                result.error?.let { e ->
                    viewState.setError(e)
                    logEvent(Analytics.EVENT_LOGIN_CODE, Analytics.RESULT_FAIL)
                }

                result.isSuccess()?.let {
                    viewState.showErrorText(false)
                    openNextScreen()
                    logEvent(Analytics.EVENT_LOGIN_CODE, Analytics.RESULT_SUCCESS)
                }
            }
            viewState.blockInterface(false)
        }
    }

    private fun logEvent(event: String, value: String) =
            analytics.logEvent(event, Analytics.STATUS, value)

    fun back() {
        router.replaceScreen(Screens.AuthorizationPager(JSON.stringify(LogInView.Params.serializer(), params)))
    }

    fun getTitleId(): Int = if (isPhone) R.string.LNG_LOGIN_SEND_SMS_CODE else R.string.LNG_LOGIN_SEND_EMAIL_CODE

    fun checkAfterPinChanged() {
        viewState.showErrorText(false)
        viewState.setBtnDoneIsEnabled(pinCode.length == pinItemsCount)
    }

    fun setTimer() {
        viewState.startTimer()
        timerBtnResendCode.start()
    }

    fun cancelTimer() {
        timerBtnResendCode.cancel()
    }

    companion object {
        const val PHONE_ATTRIBUTE = "+"
        const val EMAIL_ATTRIBUTE = "@"
        const val PIN_ITEMS_COUNT = 4
    }
}
