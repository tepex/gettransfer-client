package com.kg.gettransfer.presentation.presenter

import android.os.CountDownTimer

import moxy.InjectViewState

import com.kg.gettransfer.core.domain.Second

import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SmsCodeView

import com.kg.gettransfer.utilities.Analytics
import kotlinx.coroutines.launch

import kotlinx.serialization.json.JSON

@InjectViewState
class SmsCodePresenter : BaseLogInPresenter<SmsCodeView>() {

    var isPhone = false
    var pinCode = ""
    var pinItemsCount = PIN_ITEMS_COUNT

    val isEnabledButtonDone
        get() = pinCode.length == pinItemsCount

    private var smsResendDelay = Second(RESENT_DELAY).millis
    private lateinit var timerBtnResendCode: CountDownTimer

    override fun attachView(view: SmsCodeView) {
        super.attachView(view)
        worker.main.launch {
            smsResendDelay = configsManager.getMobileConfigs().smsResendDelay.millis
            initTimer()
            setTimer()
        }
    }

    private fun initTimer() {
        timerBtnResendCode = object : CountDownTimer(smsResendDelay, Second.MILLIS_PER_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                viewState.tickTimer(millisUntilFinished, Second.MILLIS_PER_SECOND)
            }

            override fun onFinish() {
                viewState.finishTimer()
            }
        }
    }

    fun onLoginClick() {
        analytics.logSingleEvent(Analytics.VERIFY_CODE_CLICKED)

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> accountManager.login(null, params.emailOrPhone, pinCode, true)
                    false -> accountManager.login(params.emailOrPhone, null, pinCode, true)
                }
            }.also { result ->
                viewState.blockInterface(false)

                result.error?.let { e ->
                    viewState.setError(e)
                    logEvent(Analytics.EVENT_LOGIN_CODE, Analytics.RESULT_FAIL)
                }

                result.isSuccess()?.let {
                    openNextScreen()
                    logEvent(Analytics.EVENT_LOGIN_CODE, Analytics.RESULT_SUCCESS)
                }
            }
        }
    }

    fun sendVerificationCode() {
        analytics.logSingleEvent(Analytics.RESEND_CODE_CLICKED)

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                when (isPhone) {
                    true -> sessionInteractor.getVerificationCode(null, params.emailOrPhone)
                    false -> sessionInteractor.getVerificationCode(params.emailOrPhone, null)
                }
            }.also { result ->
                viewState.blockInterface(false)

                result.error?.let { e ->
                    viewState.setError(e)
                    logEvent(Analytics.EVENT_RESEND_CODE, Analytics.RESULT_FAIL)
                }

                result.isSuccess()?.let {
                    setTimer()
                    logEvent(Analytics.EVENT_RESEND_CODE, Analytics.RESULT_SUCCESS)
                }
            }
        }
    }

    fun setTimer() {
        viewState.startTimer()
        timerBtnResendCode.start()
    }

    fun cancelTimer() {
        timerBtnResendCode.cancel()
    }

    fun back() {
        router.replaceScreen(Screens.AuthorizationPager(JSON.stringify(LogInView.Params.serializer(), params)))
    }

    companion object {
        const val PIN_ITEMS_COUNT = 4
        private const val RESENT_DELAY = 90
    }
}
