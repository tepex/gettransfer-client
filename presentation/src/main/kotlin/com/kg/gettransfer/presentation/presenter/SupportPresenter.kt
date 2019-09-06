package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.sys.domain.ContactEmail
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SupportView
import com.kg.gettransfer.utilities.Analytics

import com.kg.gettransfer.sys.presentation.ConfigsManager
import kotlinx.coroutines.launch

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class SupportPresenter : BasePresenter<SupportView>() {

    private val worker: WorkerManager by inject { parametersOf("SupportPresenter") }
    private val configsManager: ConfigsManager by inject()

    override fun attachView(view: SupportView) {
        super.attachView(view)
        showEmail()
    }

    private fun showEmail() {
        viewState.showEmail(configsManager.configs.contactEmails.first { it.id == ContactEmail.Id.INFO }.email)
    }

    fun onAboutUsClick() = worker.main.launch {
        router.navigateTo(Screens.About(getPreferences().getModel().isOnboardingShowed))
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.ABOUT_CLICKED)
    }

    fun onBecomeCarrierClick() {
        //TODO: Maybe needed analytics
        viewState.openBecomeCarrier()
    }
}
