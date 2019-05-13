package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.presentation.view.AuthDialogView

@InjectViewState
class AuthDialogPresenter: BasePresenter<AuthDialogView>() {

    val profile: Profile
    get() = systemInteractor.account.user.profile

    override fun attachView(view: AuthDialogView) {
        super.attachView(view)
        with(profile) {
            viewState.setPhone(phone ?: "", phone == null)
            viewState.setEmail(email ?: "", email == null)
        }
    }

    fun putAccount(mEmail: String, mPhone: String) {
        utils.launchSuspend {
            with(profile) {
                phone = mPhone
                email = mEmail
            }
          //  pushAccount()
        }
    }

    private suspend fun pushAccount() =
            fetchResult { systemInteractor.putAccount() }
                    .run {
                        when {
                            error != null                       -> viewState.redirectToLogin()
                            else                                -> viewState.onAccountCreated()
                        }
                    }
}