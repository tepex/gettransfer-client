package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.presentation.view.CarrierTripDetailsView
import ru.terrakok.cicerone.Router

@InjectViewState
class CarrierTripDetailsPresenter(cc: CoroutineContexts,
                                  router: Router,
                                  apiInteractor: ApiInteractor): BasePresenter<CarrierTripDetailsView>(cc, router, apiInteractor){

    private var selectedTripId: Long? = null
    private lateinit var trip: CarrierTrip

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            selectedTripId = apiInteractor.selectedTripId
            utils.asyncAwait{
                account = apiInteractor.getAccount()
                trip = apiInteractor.getCarrierTrip(selectedTripId!!)
            }
            viewState.setTripInfo(trip.transferId, trip.from.name, trip.to.name, trip.dateLocal, trip.distance,
                        trip.pax, trip.nameSign,trip.childSeats, trip.flightNumber, trip.comment, trip.remainToPay )
        }, { e ->
            if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
            else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }
}