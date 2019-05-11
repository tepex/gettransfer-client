package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.view.ChangePasswordView
import org.koin.standalone.KoinComponent

@InjectViewState
class ChangePasswordFragmentPresenter : BasePresenter<ChangePasswordView>(), KoinComponent {
    private var newPassword: String? = null
    private var repeatedNewPassword: String? = null

    fun setPassword(password: String, isRepeatedPassword: Boolean) {
        val pass = if (password.isEmpty()) null else password
        if (isRepeatedPassword) this.repeatedNewPassword = pass
        else this.newPassword = pass
        viewState.enableBtnSave(fieldsIsNotEmpty())
    }

    fun onSaveClick() {
        if (isFieldsEquals()) {
            utils.launchSuspend {
                viewState.blockInterface(true)
                val result = utils.asyncAwait { systemInteractor.changePassword(newPassword!!, repeatedNewPassword!!) }
                result.error?.let { viewState.setError(it) }
                if (result.error == null) viewState.passwordChanged()
                viewState.blockInterface(false)
            }
        } else {
            viewState.setError(false, R.string.pass_match)
        }
    }

    private fun fieldsIsNotEmpty() = !newPassword.isNullOrEmpty() && !repeatedNewPassword.isNullOrEmpty()
    private fun isFieldsEquals() = fieldsIsNotEmpty() && newPassword == repeatedNewPassword
}