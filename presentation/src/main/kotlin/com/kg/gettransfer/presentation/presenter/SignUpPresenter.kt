package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.RegistrationAccount
import com.kg.gettransfer.extensions.internationalExample

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

    fun registration() {
        if (!checkFieldsIsValid()) {
            viewState.showValidationErrorDialog(Utils.phoneUtil.internationalExample(sessionInteractor.locale))
            viewState.hideLoading()
            return
        }

        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                accountManager.register(RegistrationAccount(email, phone, termsAccepted, name))
            }.also { result ->
                result.error?.let { e ->
                    viewState.setError(e)
                    logLoginEvent(Analytics.RESULT_FAIL)
                }

                result.isSuccess()?.let {
                    viewState.showRegisterSuccessDialog()
                    logLoginEvent(Analytics.RESULT_SUCCESS)
                }
                viewState.hideLoading()
            }
        }
    }

    private fun logLoginEvent(value: String) = analytics.logEvent(Analytics.EVENT_SIGN_UP, Analytics.STATUS, value)

    private fun checkFieldsIsValid() = LoginHelper.phoneIsValid(phone) && LoginHelper.emailIsValid(email)

    fun checkFieldsIsEmpty() = name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && termsAccepted

    fun showLicenceAgreement() {
        router.navigateTo(Screens.LicenceAgree)
        viewState.hideLoading()
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
}
