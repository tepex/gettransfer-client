package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SplashView

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor
import com.kg.gettransfer.sys.domain.IsNeedUpdateAppInteractor
import com.kg.gettransfer.sys.domain.SetNewDriverAppDialogShowedInteractor
import com.kg.gettransfer.sys.domain.SetOnboardingShowedInteractor

import com.kg.gettransfer.sys.presentation.ConfigsManager

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import ru.terrakok.cicerone.Router

@InjectViewState
class SplashPresenter : MvpPresenter<SplashView>(), KoinComponent {

    private val sessionInteractor: SessionInteractor by inject()
    private val router: Router by inject()

    private val worker: WorkerManager by inject { parametersOf("SplashPresenter") }
    private val configsManager: ConfigsManager by inject()
    private val getPreferences: GetPreferencesInteractor by inject()
    private val isNeedUpdateApp: IsNeedUpdateAppInteractor by inject()
    private val setOnboardingShowed: SetOnboardingShowedInteractor by inject()
    private val setNewDriverAppDialogShowedInteractor: SetNewDriverAppDialogShowedInteractor by inject()

    fun onLaunchContinue() {
        viewState.checkLaunchType()
        worker.main.launch {
            configsManager.coldStart(worker.backgroundScope)
            withContext(worker.bg) {
                setNewDriverAppDialogShowedInteractor(false)

                val needUpdateApp = isNeedUpdateApp(
                    IsNeedUpdateAppInteractor.FIELD_UPDATE_REQUIRED,
                    BuildConfig.VERSION_CODE
                )
                if (needUpdateApp) viewState.onNeedAppUpdateInfo() else startApp()
            }
        }
    }

    fun startApp() = worker.main.launch {
        viewState.dispatchAppState(sessionInteractor.locale)
        val isOnboardingShowed = withContext(worker.bg) { getPreferences().getModel() }.isOnboardingShowed
        if (!isOnboardingShowed) {
            withContext(worker.bg) { setOnboardingShowed(true) }
        }
        goToMainScreen(isOnboardingShowed)
    }

    fun enterByPush() {
        router.newRootChain(Screens.MainPassenger(), Screens.Requests)
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }

    private fun goToMainScreen(onboardingShowed: Boolean) {
        router.newRootScreen(Screens.MainPassenger(!onboardingShowed))
    }
}
