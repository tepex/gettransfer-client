package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.sys.domain.ContactEmail
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SupportView
import com.kg.gettransfer.utilities.Analytics

import com.kg.gettransfer.sys.presentation.ConfigsManager

import org.koin.core.inject

@InjectViewState
class SupportPresenter : BasePresenter<SupportView>() {

    private val configsManager: ConfigsManager by inject()

    override fun attachView(view: SupportView) {
        super.attachView(view)
        showEmail()
    }

    private fun showEmail() {
        viewState.showEmail(configsManager.configs.contactEmails.first { it.id == ContactEmail.Id.INFO }.email)
    }

    fun onAboutUsClick() {
        router.navigateTo(Screens.About(systemInteractor.isOnboardingShowed))
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.ABOUT_CLICKED)
    }
}
