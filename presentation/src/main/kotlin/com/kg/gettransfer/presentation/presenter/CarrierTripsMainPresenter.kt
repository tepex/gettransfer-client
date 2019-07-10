package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.GeoInteractor
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.view.CarrierTripsMainView
import com.kg.gettransfer.presentation.view.CarrierTripsMainView.Companion.BG_COORDINATES_NOT_ASKED
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

@InjectViewState
class CarrierTripsMainPresenter: BasePresenter<CarrierTripsMainView>(), KoinComponent {
    private val profileMapper: ProfileMapper by inject()
    private val geoInteractor: GeoInteractor by inject()

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        systemInteractor.lastMode = Screens.CARRIER_MODE
        checkLoggedIn()
        viewState.initNavigation(profileMapper.toView(accountManager.remoteProfile))
        if (systemInteractor.lastCarrierTripsTypeView.isEmpty()) {
            systemInteractor.lastCarrierTripsTypeView = Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR
        }
        checkBackGroundCoordinateAcceptance()
    }

    @CallSuper
    override fun attachView(view: CarrierTripsMainView) {
        super.attachView(view)
        checkLoggedIn()
        changeTypeView(systemInteractor.lastCarrierTripsTypeView)
    }

    @CallSuper
    override fun detachView(view: CarrierTripsMainView?) {
        super.detachView(view)
    }

    fun changeTypeView(type: String){
        systemInteractor.lastCarrierTripsTypeView = type
        viewState.changeTypeView(type)
    }

    private fun checkLoggedIn() {
        if(!accountManager.isLoggedIn) router.newRootScreen(Screens.MainPassenger())
    }

    fun onCarrierTripsClick()   { /*router.navigateTo(Screens.CARRIER_TRIPS)*/ }
    fun onAboutClick()          = router.navigateTo(Screens.About(systemInteractor.isOnboardingShowed))
    fun readMoreClick()         = viewState.showReadMoreDialog()
    fun onSettingsClick()       = router.navigateTo(Screens.Settings)
    fun onSupportClick()        = router.navigateTo(Screens.Support)
    fun onPassengerModeClick()  = router.newRootScreen(Screens.MainPassenger())
    fun onTransfersClick()      = router.navigateTo(Screens.CarrierTransfers)

    fun onShareClick() {
        Timber.d("Share action")
        logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.SHARE)
        router.navigateTo(Screens.Share())
    }

    private fun checkBackGroundCoordinateAcceptance() {
        if (carrierTripInteractor.bgCoordinatesPermission == BG_COORDINATES_NOT_ASKED)
            viewState.askForBackGroundCoordinates()
    }
    fun permissionResult(accepted: Boolean) =
        carrierTripInteractor.permissionChanged(accepted)

    fun initGoogleApiClient() = geoInteractor.initGoogleApiClient()

    fun disconnectGoogleApiClient() = geoInteractor.disconnectGoogleApiClient()
}