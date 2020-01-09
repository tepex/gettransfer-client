package com.kg.gettransfer.presentation.presenter

import androidx.annotation.StringRes

import moxy.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.SettingsChangeEmailView

@InjectViewState
class SettingsChangeEmailPresenter : BasePresenter<SettingsChangeEmailView>() {

    var newEmail: String = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setToolbar(accountManager.remoteProfile.email)
    }

    override fun onDestroy() {
        accountManager.initTempUser()
        super.onDestroy()
    }

    fun onResendCodeClicked() {
        utils.launchSuspend {
            sendEmailCode()
        }
    }

    fun onChangeEmailClicked() {
        utils.launchSuspend {
            if (!accountManager.remoteProfile.email.isNullOrEmpty()) {
                sendEmailCode()
            } else {
                changeEmail(null)
            }
        }
    }

    fun onCodeEntered(code: String) {
        utils.launchSuspend {
            changeEmail(code)
        }
    }

    private suspend fun sendEmailCode() {
        if (!isEmailValid()) return
        val result = fetchResultOnly { sessionInteractor.getCodeForChangeEmail(newEmail) }
        if (result.error == null && result.model) {
            viewState.showCodeLayout()
            viewState.setTimer(configsManager.getMobileConfigs().smsResendDelay.millis)
        } else {
            result.error?.let { checkEmailErrors(it) }
        }
    }

    private suspend fun changeEmail(code: String?) {
        if (!isEmailValid()) return
        code?.let { changeEmailInAccount(it) } ?: setEmailInAccount()
    }

    private suspend fun setEmailInAccount() {
        accountManager.tempProfile.email = newEmail
        fetchResultOnly { accountManager.putAccount() }
            .run {
                error?.let { checkEmailErrors(it) } ?: emailChanged()
            }
    }

    private fun checkEmailErrors(e: ApiException) =
        showError(e, when {
            e.isAccountExistError()         -> R.string.LNG_EMAIL_TAKEN_ERROR
            e.isNewEmailInvalid()           -> R.string.LNG_ERROR_EMAIL
            e.isEmailNotChangeableError()   -> R.string.LNG_EMAIL_NOT_CHANGEABLE
            e.isNewEmailAlreadyTakenError() -> R.string.LNG_EMAIL_TAKEN_ERROR
            e.isNewEmailInvalid()           -> R.string.LNG_ERROR_EMAIL
            else                            -> null
        })

    private fun showError(e: ApiException, @StringRes errId: Int?) {
        errId?.let { viewState.setError(false, it) } ?: viewState.setError(e)
    }

    private suspend fun changeEmailInAccount(code: String) {
        fetchResultOnly { sessionInteractor.changeEmail(newEmail, code) }
            .run {
                error?.let { err ->
                    if (err.code == ApiException.UNPROCESSABLE) {
                        viewState.setWrongCodeError(err.details)
                    } else {
                        viewState.setError(err)
                    }
                } ?: emailChanged()
            }
    }

    private fun emailChanged() { router.exit() }

    private fun isEmailValid(): Boolean {
        return if (newEmail.isEmpty() || !Utils.checkEmail(newEmail)) {
            viewState.setError(false, R.string.LNG_ERROR_EMAIL)
            false
        } else {
            true
        }
    }
}
