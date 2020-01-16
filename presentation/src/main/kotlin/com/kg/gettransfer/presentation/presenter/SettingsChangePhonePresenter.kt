package com.kg.gettransfer.presentation.presenter

import androidx.annotation.StringRes
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.SettingsChangePhoneView
import moxy.InjectViewState

@InjectViewState
class SettingsChangePhonePresenter : BasePresenter<SettingsChangePhoneView>() {

    var newPhone = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setToolbar(accountManager.remoteProfile.phone)
    }

    override fun onDestroy() {
        accountManager.initTempUser()
        super.onDestroy()
    }

    fun onResendCodeClicked() {
        utils.launchSuspend {
            sendPhoneCode()
        }
    }

    fun onChangePhoneClicked() {
        utils.launchSuspend {
            if (!accountManager.remoteProfile.phone.isNullOrEmpty()) {
                sendPhoneCode()
            } else {
                changePhone(null)
            }
        }
    }

    fun onCodeEntered(code: String) {
        utils.launchSuspend {
            changePhone(code)
        }
    }

    private suspend fun sendPhoneCode() {
        if (!isPhoneValid()) return
        val result = fetchResultOnly { sessionInteractor.getConfirmationCode(phone = newPhone) }
        if (result.error == null && result.model) {
            val resendDelay = configsManager.getMobileConfigs().smsResendDelaySec.toLongMilliseconds()
            viewState.showCodeLayout(resendDelay)
        } else {
            result.error?.let { checkPhoneErrors(it) }
        }
    }

    private suspend fun changePhone(code: String?) {
        if (!isPhoneValid()) return
        code?.let { changePhoneInAccount(it) } ?: setPhoneInAccount()
    }

    private suspend fun setPhoneInAccount() {
        accountManager.tempProfile.phone = newPhone
        fetchResultOnly { accountManager.putAccount() }
            .run {
                error?.let { checkPhoneErrors(it) } ?: phoneChanged()
            }
    }

    private fun checkPhoneErrors(e: ApiException) =
        showError(e, when {
            e.isAccountExistError() ||
            e.isNewPhoneAlreadyTakenError() -> R.string.LNG_PHONE_TAKEN_ERROR
            e.isNewPhoneInvalid()           -> R.string.LNG_ERROR_PHONE
            e.isPhoneNotChangeableError()   -> R.string.LNG_PHONE_NOT_CHANGEABLE
            else                            -> null
        })

    private fun showError(e: ApiException, @StringRes errId: Int?) {
        errId?.let { viewState.setError(false, it) } ?: viewState.setError(e)
    }

    private suspend fun changePhoneInAccount(code: String) {
        fetchResultOnly { sessionInteractor.changeContact(code, phone = newPhone) }
            .run {
                error?.let { err ->
                    if (err.code == ApiException.UNPROCESSABLE) {
                        viewState.setWrongCodeError(err.details)
                    } else {
                        viewState.setError(err)
                    }
                } ?: phoneChanged()
            }
    }

    private fun phoneChanged() { router.exit() }

    private fun isPhoneValid(): Boolean {
        return if (newPhone.isEmpty() || !Utils.checkPhone(newPhone)) {
            viewState.setError(false, R.string.LNG_ERROR_PHONE)
            false
        } else {
            true
        }
    }
}
