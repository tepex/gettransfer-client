package com.kg.gettransfer.presentation.ui

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.view.SignUpView
import org.koin.standalone.KoinComponent

@InjectViewState
class SignUpPresenter : BasePresenter<SignUpView>(), KoinComponent {
    fun registration(name: String, phone: String, email: String, termsAccepted: Boolean) {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                accountManager.register(name, phone, email, termsAccepted)
            }
                .also {
                    it.error?.let { e ->
                        //TODO added error
//                        viewState.showError(true, e)
//                        logLoginEvent(Analytics.RESULT_FAIL)
                    }

                    it.isSuccess()?.let {
                        viewState.showRegisterSuccessDialog()
                        registerPushToken()
                    }
                }
            viewState.blockInterface(false)
        }
    }

    fun checkFieldsIsValid(phone: String, email: String): Boolean =
        LoginHelper.phoneIsValid(phone) && LoginHelper.emailIsValid(email)

    fun checkFieldsIsEmpty(fieldValues: List<String>): Boolean =
        fieldValues.all { fieldValue -> fieldValue.isNotEmpty() }
}
