package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.sys.domain.ContactEmail
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SupportView
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor
import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import moxy.InjectViewState

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class SupportPresenter : BasePresenter<SupportView>() {

    private val worker: WorkerManager by inject { parametersOf("SupportPresenter") }
    private val getPreferences: GetPreferencesInteractor by inject()

    override fun attachView(view: SupportView) {
        super.attachView(view)
        worker.main.launch {
            showEmail()
        }
    }

    private suspend fun showEmail() {
        viewState.showEmail(configsManager.getConfigs().contactEmails.first { it.id == ContactEmail.Id.INFO }.email)
    }

    fun onAboutUsClick() = worker.main.launch {
        val isOnboardingShowed = withContext(worker.bg) { getPreferences().getModel() }.isOnboardingShowed
        router.navigateTo(Screens.About(isOnboardingShowed))
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.ABOUT_CLICKED)
    }

    fun onBecomeCarrierClick() {
        analytics.logEvent(Analytics.EVENT_BECOME_CARRIER, Analytics.OPEN_SCREEN, null)
        viewState.openBecomeCarrier()
    }
}
