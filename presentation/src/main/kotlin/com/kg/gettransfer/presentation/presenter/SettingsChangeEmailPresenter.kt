package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.SettingsChangeEmailView

@InjectViewState
class SettingsChangeEmailPresenter : BasePresenter<SettingsChangeEmailView>() {

    private var newEmail: String? = null
    private var emailCode: String? = null

    private var smsSent = false

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setToolbar(accountManager.remoteProfile.email)
    }

    fun setEmail(email: String) {
        newEmail = email.trim()
        smsSent = false
        checkBtnVisibility()
    }

    fun setCode(code: String, correctAmountSymbols: Boolean) {
        emailCode = code
        checkBtnVisibility(correctAmountSymbols)
    }

    private fun checkBtnVisibility(correctAmountSymbols: Boolean = false) {
        viewState.setEnabledBtnChangeEmail(!newEmail.isNullOrEmpty() &&
                ((!emailCode.isNullOrEmpty() && correctAmountSymbols) || !smsSent))
    }

    fun onResendCodeClicked() {
        utils.launchSuspend {
            sendEmailCode()
        }
    }

    fun onChangeEmailClicked() {
        utils.launchSuspend {
            if (!smsSent) {
                if (Utils.checkEmail(newEmail)) {
                    sendEmailCode()
                } else {
                    viewState.setError(false, R.string.LNG_ERROR_EMAIL)
                }
            } else {
                changeEmail()
            }
        }
    }

    private suspend fun sendEmailCode() {
        newEmail?.let {
            val result = fetchResultOnly { sessionInteractor.getCodeForChangeEmail(it) }
            if (result.error == null && result.model) {
                viewState.showCodeLayout()
                viewState.setTimer(sessionInteractor.mobileConfigs.smsResendDelaySec * SEC_IN_MILLIS)
                smsSent = true
                checkBtnVisibility()
            } else {
                result.error?.let {
                    when {
                        it.details.indexOf("account=[email_not_manually_changeable]") >= 0 ->
                            viewState.setError(false, R.string.LNG_EMAIL_NOT_CHANGEABLE)
                        it.details.indexOf("new_email=[already_taken]") >= 0 ->
                            viewState.setError(false, R.string.LNG_EMAIL_TAKEN_ERROR)
                        else -> viewState.setError(it)
                    }
                }
            }
        }
    }

    private suspend fun changeEmail() {
        newEmail?.let { email ->
            emailCode?.let { code ->
                val result = fetchResultOnly { sessionInteractor.changeEmail(email, code) }
                if (result.error == null && result.model) {
                    router.exit()
                } else {
                    result.error?.let {
                        when {
                            it.details.indexOf("bad_code_or_email") >= 0 ->
                                viewState.setWrongCodeError()
                            else -> viewState.setError(it)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val SEC_IN_MILLIS = 1_000L
        const val MAX_CODE_LENGTH = 8
    }
}