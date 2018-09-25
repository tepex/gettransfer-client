package com.kg.gettransfer.presentation.presenter

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
        systemInteractor.putLastMode(Screens.CARRIER_MODE)
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            trips = carrierTripInteractor.getCarrierTrips().map {
                Mappers.getCarrierTripModel(it, systemInteractor.getLocale(), systemInteractor.getDistanceUnit()) }
            viewState.initNavigation(systemInteractor.account)
            viewState.setTrips(trips!!)
        }, { e ->
            if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
            else viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    fun onTripSelected(tripId: Long){
        carrierTripInteractor.selectedTripId = tripId
        router.navigateTo(Screens.TRIP_DETAILS)
    }

    fun onCarrierTripsClick()   { router.navigateTo(Screens.CARRIER_TRIPS) }
    fun onAboutClick()          { router.navigateTo(Screens.ABOUT) }
    fun readMoreClick()         { router.navigateTo(Screens.READ_MORE) }
    fun onSettingsClick()       { router.navigateTo(Screens.SETTINGS) }
    fun onPassengerModeClick()  { router.navigateTo(Screens.PASSENGER_MODE) }
}