package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.LogsInteractor
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.ui.helpers.BuildsConfigsHelper
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SplashView
import kotlinx.coroutines.Job
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.terrakok.cicerone.Router

@InjectViewState
class SplashPresenter: MvpPresenter<SplashView>(), KoinComponent {

    private val compositeDisposable = Job()
    private val coroutineContexts: CoroutineContexts by inject()
    private val utils = AsyncUtils(coroutineContexts, compositeDisposable)
    private val systemInteractor: SystemInteractor by inject()
    private val reviewInteractor: ReviewInteractor by inject()
    private val logsInteractor: LogsInteractor by inject()
    private val sessionInteractor: SessionInteractor by inject()
    private val router: Router by inject()

    fun onLaunchContinue() {
        viewState.initBuildConfigs(object :
                LateAccessLogs { override fun getLog() = logsInteractor.onLogRequested() }
        )
        /* Check PUSH notification */
        viewState.checkLaunchType()
        reviewInteractor.shouldAskRateInMarket = shouldAskForRateApp()
        onAppLaunched()
    }

    private fun onAppLaunched() {
        utils.launchSuspend {
            utils.asyncAwait { sessionInteractor.coldStart() }
            if (checkNeededUpdateApp()) viewState.onNeedAppUpdateInfo()
            else startApp()
        }
    }

    private fun checkNeededUpdateApp(): Boolean {
        sessionInteractor.mobileConfigs.buildsConfigs?.let { buildsConfigs ->
            BuildsConfigsHelper.getConfigsForCurrentBuildByField(
                    BuildsConfigsHelper.SETTINGS_FIELD_UPDATE_REQUIRED,
                    buildsConfigs
            )?.let { return it.updateRequired ?: false }
        }
        return false
    }

    fun startApp() {
        viewState.dispatchAppState()
        openStartScreen()
    }

    /* TODO: Magic values here! */
    private fun shouldAskForRateApp() = when (systemInteractor.appEntersForMarketRate) {
        3    -> true
        9    -> true
        18   -> true
        else -> false
    }

    private fun openStartScreen() {
        if (!systemInteractor.isOnboardingShowed) {
            router.replaceScreen(Screens.About(systemInteractor.isOnboardingShowed))
            systemInteractor.isOnboardingShowed = true
        }
        else when (systemInteractor.lastMode) {
            Screens.CARRIER_MODE -> router.replaceScreen(Screens.Carrier(Screens.CARRIER_MODE))
            else -> { router.newRootScreen(Screens.MainPassenger(!systemInteractor.isOnboardingShowed)) }
        }
    }

    fun enterByPush() {
        router.newRootChain(Screens.MainPassenger(), Screens.Requests)
    }

    override fun detachView(view: SplashView?) {
        super.detachView(view)
        compositeDisposable.cancel()
    }

    interface LateAccessLogs {
        fun getLog(): String
    }

}