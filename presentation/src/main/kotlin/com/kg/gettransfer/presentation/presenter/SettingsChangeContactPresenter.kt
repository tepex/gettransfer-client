package com.kg.gettransfer.presentation.presenter

import androidx.annotation.StringRes
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.Contact
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.SettingsChangeContactView
import moxy.InjectViewState

@InjectViewState
class SettingsChangeContactPresenter(
    private val isChangingEmail: Boolean
) : BasePresenter<SettingsChangeContactView>() {

    private var isCodeLayoutShowed = false

    var newContact = ""
        set(value) {
            field = value
            if (isCodeLayoutShowed) {
                isCodeLayoutShowed = false
                viewState.hideCodeLayout()
            }
        }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        with(accountManager.remoteProfile) {
            viewState.setToolbar(if (isChangingEmail) email else phone)
        }
    }

    override fun onDestroy() {
        accountManager.initTempUser()
        super.onDestroy()
    }

    fun onChangeContactClicked() {
        utils.launchSuspend {
            with(accountManager.remoteProfile) {
                if (isChangingEmail && !email.isNullOrEmpty() ||
                    !isChangingEmail && !phone.isNullOrEmpty()) {
                    sendCode()
                } else {
                    changeContact(null)
                }
            }
        }
    }

    fun onResendCodeClicked() {
        utils.launchSuspend {
            sendCode()
        }
    }

    private suspend fun sendCode() {
        if (!isDataValid()) return
        val result = fetchResultOnly { sessionInteractor.getConfirmationCode(getContact()) }
        if (result.error == null && result.model) {
            val smsDelay = configsManager.getMobileConfigs().smsResendDelaySec.toLongMilliseconds()
            isCodeLayoutShowed = true
            viewState.showCodeLayout(smsDelay)
        } else {
            result.error?.let { checkErrors(it) }
        }
    }

    fun onCodeEntered(code: String) {
        utils.launchSuspend {
            changeContact(code)
        }
    }

    private suspend fun changeContact(code: String?) {
        if (!isDataValid()) return
        code?.let { changeContactInAccount(it) } ?: setContactInAccount()
    }

    private suspend fun changeContactInAccount(code: String) {
        fetchResultOnly { sessionInteractor.changeContact(getContact(), code) }.run {
            error?.let { err ->
                if (err.code == ApiException.UNPROCESSABLE) {
                    viewState.setWrongCodeError(err.details)
                } else {
                    viewState.setError(err)
                }
            } ?: contactChanged()
        }
    }

    private suspend fun setContactInAccount() {
        if (isChangingEmail) {
            accountManager.tempProfile.email = newContact
        } else {
            accountManager.tempProfile.phone = newContact
        }
        fetchResultOnly { accountManager.putAccount() }
            .run {
                error?.let { checkErrors(it) } ?: contactChanged()
            }
    }

    private fun isDataValid() =
        if (isChangingEmail && !Utils.checkEmail(newContact)) {
            viewState.setError(false, R.string.LNG_ERROR_EMAIL)
            false
        } else if (!isChangingEmail && !Utils.checkPhone(newContact)) {
            viewState.setError(false, R.string.LNG_ERROR_PHONE)
            false
        } else {
            true
        }

    private fun checkErrors(e: ApiException) =
        showError(e, if (isChangingEmail) checkEmailErrors(e) else checkPhoneErrors(e) )


    private fun checkEmailErrors(e: ApiException) =
        when {
            e.isAccountExistError() ||
            e.isNewEmailAlreadyTakenError() -> R.string.LNG_EMAIL_TAKEN_ERROR
            e.isNewEmailInvalid()           -> R.string.LNG_ERROR_EMAIL
            e.isEmailNotChangeableError()   -> R.string.LNG_EMAIL_NOT_CHANGEABLE
            else                            -> null
        }

    private fun checkPhoneErrors(e: ApiException) =
        when {
            e.isAccountExistError() ||
            e.isNewPhoneAlreadyTakenError() -> R.string.LNG_PHONE_TAKEN_ERROR
            e.isNewPhoneInvalid()           -> R.string.LNG_ERROR_PHONE
            e.isPhoneNotChangeableError()   -> R.string.LNG_PHONE_NOT_CHANGEABLE
            else                            -> null
        }

    private fun showError(e: ApiException, @StringRes errId: Int?) {
        errId?.let { viewState.setError(false, it) } ?: viewState.setError(e)
    }

    private fun contactChanged() { router.exit() }

    private fun getContact() =
        if (isChangingEmail) Contact.EmailContact(newContact) else Contact.PhoneContact(newContact)
}