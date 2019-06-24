package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.RegistrationAccount
import com.kg.gettransfer.extensions.internationalExample

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SignUpView

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.KoinComponent

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

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_SIGN_UP, createStringBundle(Analytics.STATUS, result), map)
    }

    private fun checkFieldsIsValid(): Boolean =
        LoginHelper.phoneIsValid(phone) && LoginHelper.emailIsValid(email)

    fun checkFieldsIsEmpty(): Boolean = name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && termsAccepted

    fun showLicenceAgreement() {
        router.navigateTo(Screens.LicenceAgree)
        viewState.hideLoading()
    }
}
