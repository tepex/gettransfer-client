package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.view.SettingsChangePasswordView

@InjectViewState
class SettingsChangePasswordPresenter : BasePresenter<SettingsChangePasswordView>() {

    private var newPassword: String? = null
    private var repeatedNewPassword: String? = null

    fun setPassword(password: String, isRepeatedPassword: Boolean) {
        val pass = if (password.isEmpty()) null else password
        if (isRepeatedPassword) this.repeatedNewPassword = pass else this.newPassword = pass
        viewState.enableBtnSave(fieldsIsNotEmpty())
    }

    fun onSaveClick() {
        if (isFieldsEquals()) {
            utils.launchSuspend {
                viewState.blockInterface(true)
                val result = utils.asyncAwait { sessionInteractor.changePassword(newPassword!!, repeatedNewPassword!!) }
                result.error?.let { viewState.setError(it) }
                if (result.error == null) router.exit()
                viewState.blockInterface(false)
            }
        } else {
            viewState.setError(false, R.string.LNG_NEW_PASSWORD_FAIL)
        }
    }

    private fun fieldsIsNotEmpty() = !newPassword.isNullOrEmpty() && !repeatedNewPassword.isNullOrEmpty()
    private fun isFieldsEquals() = fieldsIsNotEmpty() && newPassword == repeatedNewPassword
}
