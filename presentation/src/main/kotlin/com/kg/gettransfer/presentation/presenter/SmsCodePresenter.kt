package com.kg.gettransfer.presentation.presenter

import android.os.CountDownTimer
import com.kg.gettransfer.domain.model.Contact

import moxy.InjectViewState

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

    private var timerBtnResendCode: CountDownTimer? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        worker.main.launch {
            initTimer()
            setTimer()
            showCodeExpiration()
        }
    }

    private suspend fun showCodeExpiration() {
        viewState.showCodeExpiration(configsManager.getConfigs().codeExpiration)
    }

    private suspend fun initTimer() {
        val smsDelay = configsManager.getMobileConfigs().smsResendDelaySec.toLongMilliseconds()
        timerBtnResendCode = object : CountDownTimer(smsDelay, MILLIS_PER_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                viewState.tickTimer(millisUntilFinished, MILLIS_PER_SECOND)
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
            val contact = when (isPhone) {
                true -> Contact.PhoneContact(params.emailOrPhone)
                false -> Contact.EmailContact(params.emailOrPhone)
            }
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                accountManager.login(contact, pinCode, true)
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
            val contact = when (isPhone) {
                true  -> Contact.PhoneContact(params.emailOrPhone)
                false -> Contact.EmailContact(params.emailOrPhone)
            }
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                sessionInteractor.getVerificationCode(contact)
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

    private fun setTimer() {
        viewState.startTimer()
        timerBtnResendCode?.start()
    }

    private fun cancelTimer() {
        timerBtnResendCode?.cancel()
    }

    fun back() {
        router.replaceScreen(Screens.AuthorizationPager(JSON.stringify(LogInView.Params.serializer(), params)))
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
    }

    companion object {
        const val PIN_ITEMS_COUNT = 4
        const val MILLIS_PER_SECOND = 1_000L
    }
}
