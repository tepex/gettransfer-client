package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.domain.model.RegistrationAccount

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SignUpView

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.KoinComponent

@InjectViewState
class SignUpPresenter : BasePresenter<SignUpView>(), KoinComponent {
    var phone = ""
        set(value) {
            field = value.replace(" ", "")
        }
    var email = ""
        set(value) {
            field = value.trim()
        }
    var name = ""
        set(value) {
            field = value.trim()
        }
    var termsAccepted = false

    val isEnabledSignUpButton
        get() = name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && termsAccepted

    fun registration() {
        if (!checkFieldsIsValid()) return

        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                accountManager.register(RegistrationAccount(email, phone, termsAccepted, name))
            }.also { result ->
                viewState.blockInterface(false)

                result.error?.let { e ->
                    viewState.setError(e)
                    logLoginEvent(Analytics.RESULT_FAIL)
                }

                result.isSuccess()?.let {
                    viewState.showRegisterSuccessDialog()
                    logLoginEvent(Analytics.RESULT_SUCCESS)
                }
            }
        }
    }

    private fun checkFieldsIsValid(): Boolean {
        val isValid = LoginHelper.phoneIsValid(phone) && LoginHelper.emailIsValid(email)
        if (!isValid) {
            val phoneExample = Utils.getPhoneNumberExample(sessionInteractor.locale.language)
            viewState.showValidationErrorDialog(phoneExample)
        }
        return isValid
    }

    fun updateEmailOrPhone(emailOrPhone: String, isPhone: Boolean) {
        if (isPhone) {
            viewState.updateTextPhone(emailOrPhone)
        } else {
            viewState.updateTextEmail(emailOrPhone)
        }
    }

    fun onCreateTransferClick() {
        sessionInteractor.notifyCreateTransfer()
    }

    fun showLicenceAgreement() {
        router.navigateTo(Screens.LicenceAgree)
    }

    private fun logLoginEvent(value: String) = analytics.logEvent(Analytics.EVENT_SIGN_UP, Analytics.STATUS, value)
}
