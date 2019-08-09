package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.interactor.GeoInteractor

import com.kg.gettransfer.presentation.mapper.ProfileMapper

import com.kg.gettransfer.presentation.view.CarrierTripsMainView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.sys.domain.SetBackgroundCoordinatesInteractor
import com.kg.gettransfer.sys.domain.SetLastCarrierTripsTypeViewInteractor
import com.kg.gettransfer.sys.domain.SetLastModeInteractor
import com.kg.gettransfer.sys.presentation.ConfigsManager

import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class CarrierTripsMainPresenter: BasePresenter<CarrierTripsMainView>(), KoinComponent {

    private val worker: WorkerManager by inject { parametersOf("CarrierTripsMainPresenter") }
    private val profileMapper: ProfileMapper by inject()
    private val geoInteractor: GeoInteractor by inject()
    private val configsManager: ConfigsManager by inject()
    private val setLastMode: SetLastModeInteractor by inject()
    private val setLastCarrierTripsTypeView: SetLastCarrierTripsTypeViewInteractor by inject()
    private val setBackgroundCoordinates: SetBackgroundCoordinatesInteractor by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        worker.main.launch {
            withContext(worker.bg) { setLastMode(Screens.CARRIER_MODE) }
            checkLoggedIn()
            viewState.initNavigation(profileMapper.toView(accountManager.remoteProfile))
            if (getPreferences().getModel().lastCarrierTripsTypeView.isEmpty()) {
                withContext(worker.bg) {
                    setLastCarrierTripsTypeView(Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR)
                }
            }
        }
        checkBackGroundCoordinateAcceptance()
    }

    override fun attachView(view: CarrierTripsMainView) {
        super.attachView(view)
        worker.main.launch {
            checkLoggedIn()
            changeTypeView(getPreferences().getModel().lastCarrierTripsTypeView)
        }
    }

    fun changeTypeView(type: String) {
        worker.main.launch {
            withContext(worker.bg) { setLastCarrierTripsTypeView(type) }
        }
        viewState.changeTypeView(type)
    }

    private fun checkLoggedIn() {
        if (!accountManager.isLoggedIn) {
            router.newRootScreen(Screens.MainPassenger())
        }
    }

    fun onCarrierTripsClick()   { /*router.navigateTo(Screens.CARRIER_TRIPS)*/ }

    fun onAboutClick() = worker.main.launch {
        router.navigateTo(Screens.About(getPreferences().getModel().isOnboardingShowed))
    }

    fun readMoreClick()         = viewState.showReadMoreDialog()
    fun onSettingsClick()       = router.navigateTo(Screens.Settings)
    fun onSupportClick()        = router.navigateTo(Screens.Support)
    fun onPassengerModeClick()  = router.newRootScreen(Screens.MainPassenger())
    fun onTransfersClick()      = router.navigateTo(Screens.CarrierTransfers)

    fun onShareClick() {
        log.debug("Share action")
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.SHARE)
        router.navigateTo(Screens.Share())
    }

    private fun checkBackGroundCoordinateAcceptance() = worker.main.launch {
        if (getPreferences().getModel().backgroundCoordinatesAccepted == null) {
            viewState.askForBackGroundCoordinates()
        }
    }

    fun permissionResult(accepted: Boolean) = worker.main.launch {
        withContext(worker.bg) { setBackgroundCoordinates(accepted) }
    }

    fun initGoogleApiClient() = geoInteractor.initGoogleApiClient()

    fun disconnectGoogleApiClient() = geoInteractor.disconnectGoogleApiClient()

    fun checkDriverAppNotify() {
        if (configsManager.mobile.isDriverAppNotify) {
            viewState.showDriverAppNotify()
        }
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
