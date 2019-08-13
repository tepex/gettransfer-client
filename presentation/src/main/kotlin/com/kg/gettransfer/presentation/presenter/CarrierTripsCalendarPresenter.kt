package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.interactor.CarrierTripInteractor
import com.kg.gettransfer.domain.model.CarrierTripBase

import com.kg.gettransfer.presentation.mapper.CarrierTripsCalendarItemsMapper
import com.kg.gettransfer.presentation.model.CarrierTripBaseModel
import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.view.CarrierTripsCalendarFragmentView
import com.kg.gettransfer.presentation.view.Screens

import java.util.Calendar

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class CarrierTripsCalendarPresenter : BasePresenter<CarrierTripsCalendarFragmentView>() {

    private val worker: WorkerManager by inject { parametersOf("CarrierTripsCalendarPresenter") }

    private val carrierTripsCalendarItemsMapper: CarrierTripsCalendarItemsMapper by inject()

    private var carrierTripsCalendarItems: Map<String, List<CarrierTripBaseModel>>? = null

    var selectedDate = SystemUtils.formatDateWithoutTime(Calendar.getInstance().time)

    var firstDayOfWeek = 1

    override fun onFirstViewAttach() {
        worker.main.launch {
            firstDayOfWeek = getPreferences().getModel().firstDayOfWeek
        }
    }

    override fun attachView(view: CarrierTripsCalendarFragmentView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(true)
            fetchData { carrierTripInteractor.getCarrierTrips() }?.let { setCalendar(it) }
            viewState.blockInterface(false)
        }
    }

    private fun setCalendar(list: List<CarrierTripBase>) {
        carrierTripsCalendarItems = carrierTripsCalendarItemsMapper.toCalendarView(list)
        worker.main.launch {
            viewState.setCalendarIndicators(carrierTripsCalendarItems!!)
        }
        onDateClick(selectedDate)
    }

    fun onDateClick(date: String){
        selectedDate = date
        viewState.selectDate(selectedDate)
        if (!carrierTripsCalendarItems.isNullOrEmpty()) {
            val dailyTrips = carrierTripsCalendarItems!![date]
            viewState.setItemsInRVDailyTrips(dailyTrips?: emptyList())
        }
    }

    fun onTripSelected(tripId: Long, transferId: Long) = router.navigateTo(Screens.TripDetails(tripId, transferId))

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
