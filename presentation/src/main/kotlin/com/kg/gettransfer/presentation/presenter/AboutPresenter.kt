package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.presentation.view.AboutView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.sys.domain.SetFirstLaunchInteractor
import com.kg.gettransfer.sys.presentation.ConfigsManager

import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class AboutPresenter : BasePresenter<AboutView>() {

    private val configsManager: ConfigsManager by inject()
    private val setFirstLaunch: SetFirstLaunchInteractor by inject()
    private val worker: WorkerManager by inject { parametersOf("AboutPresenter") }

    internal var openMain = false

    fun closeAboutActivity() {
        if (!openMain) router.newRootScreen(Screens.MainPassenger(true)) else router.exit()
    }

    fun logExitStep(value: Int) = worker.main.launch {
        if (getPreferences().getModel().isFirstLaunch) {
            withContext(worker.bg) { setFirstLaunch(false) }
            analytics.logEvent(Analytics.EVENT_ONBOARDING, Analytics.EXIT_STEP, value)
        }
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
