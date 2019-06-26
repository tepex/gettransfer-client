package com.kg.gettransfer.presentation.presenter

import android.os.CountDownTimer
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.firstSign
import com.kg.gettransfer.extensions.internationalExample
import com.kg.gettransfer.presentation.ui.MainLoginActivity.Companion.INVALID_PHONE
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.CREDENTIALS_VALID
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.INVALID_EMAIL
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SmsCodeView
import com.kg.gettransfer.utilities.Analytics
import kotlinx.serialization.json.JSON

@InjectViewState
class SmsCodePresenter : LogInBasePresenter<SmsCodeView>() {

    var isPhone = false
    var pinCode = ""
    var pinItemsCount = PIN_ITEMS_COUNT
    val smsResendDelaySec
        get() = sessionInteractor.mobileConfigs.smsResendDelaySec * SEC_IN_MILLIS

    private val timerBtnResendCode: CountDownTimer = object : CountDownTimer(smsResendDelaySec, SEC_IN_MILLIS) {
        override fun onTick(millisUntilFinished: Long) {
            viewState.tickTimer(millisUntilFinished, SEC_IN_MILLIS)
        }

        override fun onFinish() {
            viewState.finishTimer()
        }
    }

    override fun attachView(view: SmsCodeView) {
        super.attachView(view)
        setTimer()
    }

    override fun detachView(view: SmsCodeView) {
        super.detachView(view)
        timerBtnResendCode.cancel()
    }

    fun sendVerificationCode() {
        if (!checkInputData()) return

        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> sessionInteractor.getVerificationCode(null, LoginHelper.formatPhone(params.emailOrPhone))
                    false -> sessionInteractor.getVerificationCode(params.emailOrPhone, null)
                }
            }.also { result ->
                if (result.error != null) {
                    viewState.setError(result.error!!)
                } else {
                    setTimer()
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

    private fun validateInput(): Boolean {
        LoginHelper.validateInput(params.emailOrPhone, isPhone) //force unwrap because null-check is already done
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
                    true -> accountManager.login(null, LoginHelper.formatPhone(params.emailOrPhone), pinCode, true)
                    false -> accountManager.login(params.emailOrPhone, null, pinCode, true)
                }
            }.also {
                it.error?.let { e ->
                    viewState.setError(e)
                    logLoginEvent(Analytics.RESULT_FAIL)
                }

                it.isSuccess()?.let {
                    viewState.showErrorText(false)
                    openNextScreen()
                    logLoginEvent(Analytics.RESULT_SUCCESS)
                    registerPushToken()
                    router.exit()
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

    companion object {
        const val PHONE_ATTRIBUTE = "+"
        const val EMAIL_ATTRIBUTE = "@"
        const val SEC_IN_MILLIS = 1_000L
        const val PIN_ITEMS_COUNT = 4
    }
}
