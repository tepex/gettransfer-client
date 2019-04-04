package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.view.CarrierTripsMainView
import com.kg.gettransfer.presentation.view.CarrierTripsMainView.Companion.BG_COORDINATES_NOT_ASKED
import com.kg.gettransfer.presentation.view.Screens
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

@InjectViewState
class CarrierTripsMainPresenter: BasePresenter<CarrierTripsMainView>(), KoinComponent {
    private val profileMapper: ProfileMapper by inject()

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        systemInteractor.lastMode = Screens.CARRIER_MODE
        checkLoggedIn()
        viewState.initNavigation(profileMapper.toView(systemInteractor.account.user.profile))
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
        if(!systemInteractor.account.user.loggedIn) router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
    }

    fun onCarrierTripsClick()   { /*router.navigateTo(Screens.CARRIER_TRIPS)*/ }
    fun onAboutClick()          = router.navigateTo(Screens.About(false))
    fun readMoreClick()         = viewState.showReadMoreDialog()
    fun onSettingsClick()       = router.navigateTo(Screens.Settings)
    fun onSupportClick()        = router.navigateTo(Screens.Support)
    fun onPassengerModeClick()  = router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
    fun onTransfersClick()      = router.navigateTo(Screens.CarrierTransfers)

    private fun checkBackGroundCoordinateAcceptance() {
        if (carrierTripInteractor.bgCoordinatesPermission == BG_COORDINATES_NOT_ASKED)
            viewState.askForBackGroundCoordinates()
    }
    fun permissionResult(accepted: Boolean) =
        carrierTripInteractor.permissionChanged(accepted)
}