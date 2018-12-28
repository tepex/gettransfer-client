package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.CarrierTripInteractor
import com.kg.gettransfer.presentation.mapper.CarrierTripsCalendarItemsMapper
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.view.CarrierTripsCalendarFragmentView
import com.kg.gettransfer.presentation.view.Screens
import org.koin.standalone.inject
import java.util.Calendar

@InjectViewState
class CarrierTripsCalendarFragmentPresenter : BasePresenter<CarrierTripsCalendarFragmentView>() {
    private val carrierTripInteractor: CarrierTripInteractor by inject()

    private val carrierTripsCalendarItemsMapper: CarrierTripsCalendarItemsMapper by inject()

    private var carrierTripsCalendarItems: Map<String, List<CarrierTripBaseModel>>? = null

    var selectedDate = SystemUtils.formatDateWithoutTime(Calendar.getInstance().time)

    override fun onFirstViewAttach() {
        utils.launchSuspend {
            viewState.blockInterface(true)
            val result = utils.asyncAwait { carrierTripInteractor.getCarrierTrips() }
            if (result.error != null) viewState.setError(result.error!!)
            else {
                carrierTripsCalendarItems = carrierTripsCalendarItemsMapper.toCalendarView(result.model)
                viewState.setCalendarIndicators(carrierTripsCalendarItems!!)
                onDateClick(selectedDate)
            }
            viewState.blockInterface(false)
        }
    }

    fun onDateClick(date: String){
        selectedDate = date
        if (!carrierTripsCalendarItems.isNullOrEmpty()) {
            val dailyTrips = carrierTripsCalendarItems!![date]
            viewState.setItemsInRVDailyTrips(dailyTrips?: listOf(), selectedDate)
        }
    }

    fun onTripSelected(tripId: Long, transferId: Long) = router.navigateTo(Screens.TripDetails(tripId, transferId))
}