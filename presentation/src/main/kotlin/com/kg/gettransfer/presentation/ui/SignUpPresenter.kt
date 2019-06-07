package com.kg.gettransfer.presentation.ui

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.model.RegistrationAccount
import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SignUpView
import com.kg.gettransfer.utilities.Analytics
import org.koin.standalone.KoinComponent

@InjectViewState
class SignUpPresenter : BasePresenter<SignUpView>(), KoinComponent {
    fun registration(name: String, phone: String, email: String, termsAccepted: Boolean) {
        if (!checkFieldsIsValid(phone, email)) {
            viewState.showValidationErrorDialog()
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
                    viewState.showRegisterSuccessDialog()
                    registerPushToken()
                    logLoginEvent(Analytics.RESULT_SUCCESS)
                }
            }
            viewState.blockInterface(false)
        }
    }

    private fun logLoginEvent(result: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = result

        analytics.logEvent(Analytics.EVENT_SIGN_UP, createStringBundle(Analytics.STATUS, result), map)
    }

    private fun checkFieldsIsValid(phone: String, email: String): Boolean =
        LoginHelper.phoneIsValid(phone) && LoginHelper.emailIsValid(email)

    fun checkFieldsIsEmpty(fieldValues: List<String>): Boolean =
        fieldValues.all { fieldValue -> fieldValue.isNotEmpty() }

    fun showLicenceAgreement() = router.navigateTo(Screens.LicenceAgree)
}
