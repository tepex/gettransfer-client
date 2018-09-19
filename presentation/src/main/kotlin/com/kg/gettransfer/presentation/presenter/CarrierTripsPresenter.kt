package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.CarrierTrip
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.CarrierTripsView
import ru.terrakok.cicerone.Router
import java.text.Format
import java.text.SimpleDateFormat

@InjectViewState
class CarrierTripsPresenter(cc: CoroutineContexts,
                            router: Router,
                            systemInteractor: SystemInteractor,
                            private val apiInteractor: ApiInteractor): BasePresenter<CarrierTripsView>(cc, router, systemInteractor){

    private var dateTimeFormat: SimpleDateFormat? = null
    private lateinit var trips: List<CarrierTrip>

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            dateTimeFormat = SimpleDateFormat(Utils.DATE_TIME_PATTERN, systemInteractor.getLocale())
            utils.asyncAwait{
                trips = apiInteractor.getCarrierTrips()
            }
            viewState.setTrips(trips, systemInteractor.getDistanceUnit(), dateTimeFormat!!)
        }, { e ->
            if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
            else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }

    fun onTripSelected(tripId: Long){
        apiInteractor.selectedTripId = tripId
        router.navigateTo(Screens.TRIP_DETAILS)
    }
}