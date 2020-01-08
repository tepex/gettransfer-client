package com.kg.gettransfer.presentation.presenter

import androidx.annotation.StringRes

import moxy.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.SettingsChangeEmailView

@InjectViewState
class SettingsChangeEmailPresenter : BasePresenter<SettingsChangeEmailView>() {

    private var newEmail: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setToolbar(accountManager.remoteProfile.email)
    }

    override fun onDestroy() {
        accountManager.initTempUser()
        super.onDestroy()
    }

    fun setEmail(email: String) {
        newEmail = email.trim()
        viewState.setEnabledBtnChangeEmail(!newEmail.isNullOrEmpty())
    }

    fun onResendCodeClicked() {
        utils.launchSuspend {
            sendEmailCode()
        }
    }

    fun onChangeEmailClicked() {
        if (!Utils.checkEmail(newEmail)) {
            viewState.setError(false, R.string.LNG_ERROR_EMAIL)
            return
        }
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
        newEmail?.let {
            val result = fetchResultOnly { sessionInteractor.getCodeForChangeEmail(it) }
            if (result.error == null && result.model) {
                viewState.showCodeLayout()
                viewState.setTimer(configsManager.getMobileConfigs().smsResendDelay.millis)
            } else {
                result.error?.let { checkEmailErrors(it) }
            }
        }
    }

    private suspend fun changeEmail(code: String?) {
        code?.let { changeEmailInAccount(it) } ?: setEmailInAccount()
    }

    private suspend fun setEmailInAccount() {
        accountManager.tempProfile.email = newEmail
        fetchResultOnly { accountManager.putAccount() }
            .run {
                if (error != null) checkEmailErrors(error!!) else emailChanged()
            }
    }

    private fun checkEmailErrors(e: ApiException) =
        showError(e, when {
            e.isAccountExistError() -> R.string.LNG_EMAIL_TAKEN_ERROR
            e.isNewEmailInvalid() -> R.string.LNG_ERROR_EMAIL
            e.isEmailNotChangeableError() -> R.string.LNG_EMAIL_NOT_CHANGEABLE
            e.isNewEmailAlreadyTakenError() -> R.string.LNG_EMAIL_TAKEN_ERROR
            e.isNewEmailInvalid() -> R.string.LNG_ERROR_EMAIL
            else -> null
        })

    private fun showError(e: ApiException, @StringRes errId: Int?) {
        if (errId != null) viewState.setError(false, errId) else viewState.setError(e)
    }

    private suspend fun changeEmailInAccount(code: String) {
        fetchResultOnly { sessionInteractor.changeEmail(newEmail!!, code) }
            .run {
                when {
                    error?.isBadCodeError() ?: false -> viewState.setWrongCodeError()
                    error != null                    -> viewState.setError(error!!)
                    else                             -> emailChanged()
                }
            }
    }

    private fun emailChanged() { router.exit() }
}
