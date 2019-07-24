package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.model.ContactEmail
import com.kg.gettransfer.presentation.view.SupportView

@InjectViewState
class SupportPresenter: BasePresenter<SupportView>() {

    override fun attachView(view: SupportView) {
        super.attachView(view)
        showEmail()
    }

    private fun showEmail() {
        val email = systemInteractor.contactEmails
            .first { it.id == ContactEmail.EmailId.INFO }
            .email

        viewState.showEmail(email)
    }
}