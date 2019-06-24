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

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        accountManager.initTempUser()
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
            if (!smsSent && !accountManager.remoteProfile.email.isNullOrEmpty()) {
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
                        it.isEmailNotChangebleError() ->
                            viewState.setError(false, R.string.LNG_EMAIL_NOT_CHANGEABLE)
                        it.isEmailAlreadyTakenError() ->
                            viewState.setError(false, R.string.LNG_EMAIL_TAKEN_ERROR)
                        else -> viewState.setError(it)
                    }
                }
            }
        }
    }

    private suspend fun changeEmail() {
        if (!Utils.checkEmail(newEmail)) {
            viewState.setError(false, R.string.LNG_ERROR_EMAIL)
            return
        }

        if (accountManager.remoteProfile.email.isNullOrEmpty()) {
            setEmailInAccount()
        } else {
            changeEmailInAccount()
        }
    }

    private suspend fun setEmailInAccount() {
        accountManager.tempProfile.email = newEmail
        fetchResultOnly { accountManager.putAccount(true, updateTempUser = true) }
            .run {
                when {
                    error?.isAccountExistError() ?: false -> viewState.setError(false, R.string.LNG_EMAIL_TAKEN_ERROR)
                    error != null -> viewState.setError(error!!)
                    else -> emailChanged()
                }
            }
    }

    private suspend fun changeEmailInAccount() {
        emailCode?.let { code ->
            fetchResultOnly { sessionInteractor.changeEmail(newEmail!!, code) }
                .run {
                    when {
                        error?.isBadCodeError() ?: false -> viewState.setWrongCodeError()
                        error != null -> viewState.setError(error!!)
                        else -> emailChanged()
                    }
                }
        }
    }

    private fun emailChanged() { router.exit() }

    companion object {
        const val SEC_IN_MILLIS = 1_000L
        const val MAX_CODE_LENGTH = 8
    }
}