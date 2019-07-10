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
            return
        }

        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                accountManager.register(RegistrationAccount(email, phone, termsAccepted, name))
            }.also {
                it.error?.let { e ->
                    viewState.setError(e)
                    logLoginEvent(Analytics.RESULT_FAIL)
                }

                it.isSuccess()?.let {
                    viewState.hideLoading()
                    viewState.showRegisterSuccessDialog()
                    registerPushToken()
                    logLoginEvent(Analytics.RESULT_SUCCESS)
                }
            }
        }
    }

    private fun logLoginEvent(value: String) =
        logEvent(Analytics.EVENT_SIGN_UP, Analytics.STATUS, value)

    private fun checkFieldsIsValid(): Boolean =
        LoginHelper.phoneIsValid(phone) && LoginHelper.emailIsValid(email)

    fun checkFieldsIsEmpty(): Boolean = name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && termsAccepted

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
}
