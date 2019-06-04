package com.kg.gettransfer.presentation.ui

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.data.model.ProfileEntity
import com.kg.gettransfer.data.model.UserEntity
import com.kg.gettransfer.domain.model.RegistrationAccount
import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SignUpView
import org.koin.standalone.KoinComponent

@InjectViewState
class SignUpPresenter : BasePresenter<SignUpView>(), KoinComponent {

    //TODO добавить сохранение в модель
    fun registration(name: String, phone: String, email: String, termsAccepted: Boolean) {
        if (!checkFieldsIsValid(phone, email)) {
            viewState.showValidationErrorDialog()
            return
        }

        utils.launchSuspend {
            fetchResult(SHOW_ERROR, checkLoginError = false) {
                accountManager.register(RegistrationAccount(email, phone, termsAccepted, name))
            }
                .also {
                    it.error?.let { e ->
                        viewState.setError(e)
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

    private fun checkFieldsIsValid(phone: String, email: String): Boolean =
        LoginHelper.phoneIsValid(phone) && LoginHelper.emailIsValid(email)

    fun checkFieldsIsEmpty(fieldValues: List<String>): Boolean =
        fieldValues.all { fieldValue -> fieldValue.isNotEmpty() }

    fun showLicenceAgreement() = router.navigateTo(Screens.LicenceAgree)
}
