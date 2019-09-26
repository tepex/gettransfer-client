package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SplashView

import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor
import com.kg.gettransfer.sys.domain.IsNeedUpdateAppInteractor
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
    private val isNeedUpdateApp: IsNeedUpdateAppInteractor by inject()
    private val setOnboardingShowed: SetOnboardingShowedInteractor by inject()
    private val getPreferences: GetPreferencesInteractor by inject()

    fun onLaunchContinue() {
        /* Check PUSH notification */
        viewState.checkLaunchType()
        worker.main.launch {
             val result = configsManager.coldStart(worker.backgroundScope)
             // check result for network error

             val needUpdateApp = withContext(worker.bg) {
                isNeedUpdateApp(IsNeedUpdateAppInteractor.FIELD_UPDATE_REQUIRED, BuildConfig.VERSION_CODE)
            }
            if (needUpdateApp) viewState.onNeedAppUpdateInfo() else startApp()
        }
    }

    fun startApp() = worker.main.launch {
        viewState.dispatchAppState(sessionInteractor.locale)
        val isOnboardingShowed = getPreferences().getModel().isOnboardingShowed
        if (!isOnboardingShowed) {
            router.replaceScreen(Screens.About(false))
            withContext(worker.bg) { setOnboardingShowed(true) }
        } else {
            router.newRootScreen(Screens.MainPassenger())
        }
    }

    fun enterByPush() {
        router.newRootChain(Screens.MainPassenger(), Screens.Requests)
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
