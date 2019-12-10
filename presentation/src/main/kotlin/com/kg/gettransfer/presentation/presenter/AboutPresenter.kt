package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.presentation.view.AboutView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor
import com.kg.gettransfer.sys.domain.SetFirstLaunchInteractor

import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class AboutPresenter : BasePresenter<AboutView>() {

    private val setFirstLaunch: SetFirstLaunchInteractor by inject()
    private val worker: WorkerManager by inject { parametersOf("AboutPresenter") }
    private val getPreferences: GetPreferencesInteractor by inject()

    fun closeAboutActivity() {
        worker.main.launch {
            val isOnboardingShowed = withContext(worker.bg) { getPreferences().getModel() }.isOnboardingShowed
            if (!isOnboardingShowed) router.newRootScreen(Screens.MainPassenger()) else viewState.navigateUp()
        }
    }

    fun logExitStep(value: Int) = worker.main.launch {
        val isFirstLaunch = withContext(worker.bg) { getPreferences().getModel() }.isFirstLaunch
        if (isFirstLaunch) {
            withContext(worker.bg) { setFirstLaunch(false) }
            analytics.logEvent(Analytics.EVENT_ONBOARDING, Analytics.EXIT_STEP, value)
        }
    }

    override fun onBackCommandClick() {
        viewState.onBackPressed()
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
