package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.CarrierTripInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.Mappers

import com.kg.gettransfer.presentation.view.CarrierTripsView

import ru.terrakok.cicerone.Router

@InjectViewState
class CarrierTripsPresenter(cc: CoroutineContexts,
                            router: Router,
                            systemInteractor: SystemInteractor,
                            private val carrierTripInteractor: CarrierTripInteractor): BasePresenter<CarrierTripsView>(cc, router, systemInteractor){

    private var trips: List<CarrierTripModel>? = null

    override fun onFirstViewAttach() {
        checkLoggedIn()
        systemInteractor.lastMode = Screens.CARRIER_MODE
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { carrierTripInteractor.getCarrierTrips() }
            if(result.error != null) viewState.setError(result.error!!)
            else {
                trips = result.model.map { Mappers.getCarrierTripModel(it, systemInteractor.locale, systemInteractor.distanceUnit) }
                viewState.initNavigation(Mappers.getProfileModel(systemInteractor.account.user.profile))
                viewState.setTrips(trips!!)
            }
            viewState.blockInterface(false)
        }
    }

    @CallSuper
    override fun attachView(view: CarrierTripsView) {
        super.attachView(view)
        checkLoggedIn()
    }

    fun onTripSelected(tripId: Long){
        carrierTripInteractor.selectedTripId = tripId
        router.navigateTo(Screens.TRIP_DETAILS)
    }

    fun checkLoggedIn() {
        if(!systemInteractor.account.user.loggedIn) router.navigateTo(Screens.PASSENGER_MODE)
    }

    fun onCarrierTripsClick()   { router.navigateTo(Screens.CARRIER_TRIPS) }
    fun onAboutClick()          { router.navigateTo(Screens.ABOUT) }
    fun readMoreClick()         { router.navigateTo(Screens.READ_MORE) }
    fun onSettingsClick()       { router.navigateTo(Screens.SETTINGS) }
    fun onPassengerModeClick()  { router.navigateTo(Screens.PASSENGER_MODE) }
}
