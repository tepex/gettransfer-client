package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.presentation.view.CarrierTripsView
import ru.terrakok.cicerone.Router

@InjectViewState
class CarrierTripsPresenter(cc: CoroutineContexts,
                            router: Router,
                            apiInteractor: ApiInteractor): BasePresenter<CarrierTripsView>(cc, router, apiInteractor){

    private lateinit var trips: List<CarrierTrip>

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            utils.asyncAwait{
                account = apiInteractor.getAccount()
                trips = apiInteractor.getCarrierTrips()
            }
            viewState.setTrips(trips, account.distanceUnit)
        }, { e ->
            if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
            else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }
}