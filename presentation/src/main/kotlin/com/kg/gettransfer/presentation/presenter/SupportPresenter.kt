package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.model.ContactEmail
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SupportView
import com.kg.gettransfer.utilities.Analytics

@InjectViewState
class SupportPresenter : BasePresenter<SupportView>() {

    override fun attachView(view: SupportView) {
        super.attachView(view)
        viewState.showEmail(systemInteractor.contactEmails.first { it.id == ContactEmail.EmailId.INFO }.email)
    }

    fun onAboutUsClick() {
        router.navigateTo(Screens.About(systemInteractor.isOnboardingShowed))
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.ABOUT_CLICKED)
    }
}
