package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.OrderInteractor

import com.kg.gettransfer.presentation.model.PopularPlace

import com.kg.gettransfer.presentation.view.SearchView
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor

import com.kg.gettransfer.utilities.Analytics

import com.kg.gettransfer.sys.domain.SetAddressHistoryInteractor
import com.kg.gettransfer.sys.domain.SetSelectedFieldInteractor

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class SearchPresenter : MvpPresenter<SearchView>(), KoinComponent {

    private val worker: WorkerManager by inject { parametersOf("SearchPresenter") }
    private val orderInteractor: OrderInteractor by inject()
    private val setSelectedField: SetSelectedFieldInteractor by inject()
    private val setAddressHistory: SetAddressHistoryInteractor by inject()
    protected val getPreferences: GetPreferencesInteractor by inject()
    protected val analytics: Analytics by inject()

    internal var isTo = false

    fun init() {
        viewState.setAddressFrom(orderInteractor.from?.cityPoint?.name ?: "", true, !isTo)
        if (orderInteractor.hourlyDuration == null) {
            viewState.setAddressTo(orderInteractor.to?.cityPoint?.name ?: "", true, isTo)
        } else {
            viewState.changeViewToHourlyDuration(orderInteractor.hourlyDuration)
        }
        onSearchFieldEmpty()
    }

    fun onSearchFieldEmpty() {
        worker.main.launch {
            viewState.setSuggestedAddresses(getPreferences().getModel().addressHistory)
        }
    }

    fun onPopularSelected(selected: PopularPlace) {
        analytics.logSingleEvent(Analytics.PREDEFINED_CLICKED + selected.title.toLowerCase())
        viewState.onFindPopularPlace(isTo, selected.title)
    }

    fun onAddressSelected(selected: GTAddress) {
        worker.main.launch{
            val oldAddress = if (isTo) orderInteractor.to else orderInteractor.from //for detecting double click
            var updatedGTAddress: GTAddress? = null //address with point
            selected.cityPoint.placeId?.let { placeId ->
                withContext(worker.bg) { orderInteractor.updatePoint(isTo, placeId) }.isSuccess()?.let { updatedGTAddress = it }
            }
            val newAddress = updatedGTAddress ?: selected
            analytics.logSingleEvent(Analytics.LAST_PLACE_CLICKED)
            val placeType = checkPlaceType(newAddress)
            val isDoubleClickOnRoute =
                oldAddress?.cityPoint?.point?.let { it == updatedGTAddress?.cityPoint?.point } ?: false

            if (isTo) {
                viewState.setAddressTo(
                    newAddress.variants?.first ?: newAddress.cityPoint.name,
                    placeType == ROUTE_TYPE,
                    true
                )
                orderInteractor.to = newAddress
            } else {
                viewState.setAddressFrom(
                    newAddress.variants?.first ?: newAddress.cityPoint.name,
                    placeType == ROUTE_TYPE,
                    true
                )
                orderInteractor.from = newAddress
            }

            if (placeType != ROUTE_TYPE || isDoubleClickOnRoute) {
                viewState.markFieldFilled(isTo)
                if (newAddress.cityPoint.point != null) {
                    pointReady(checkZeroPoint(newAddress), isDoubleClickOnRoute, true)
                } else if (selected.cityPoint.placeId != null) {
                    withContext(worker.bg) { orderInteractor.updatePoint(isTo, selected.cityPoint.placeId!!) }
                        .isSuccess()?.let { pointReady(checkZeroPoint(it), isDoubleClickOnRoute, true) }
                }
            } else {
                val sendRequest = newAddress.needApproximation() /* dirty hack */

                if (isTo) {
                    viewState.setAddressTo(newAddress.variants?.first ?: newAddress.cityPoint.name, sendRequest, true)
                } else {
                    viewState.setAddressFrom(newAddress.variants?.first ?: newAddress.cityPoint.name, sendRequest, true)
                }
            }
        }
    }

    fun showHourlyDurationDialog() {
        viewState.showHourlyDurationDialog(orderInteractor.hourlyDuration)
    }

    fun updateDuration(hours: Int?) {
        orderInteractor.apply {
            hourlyDuration = hours
            viewState.setHourlyDuration(hourlyDuration)
        }
    }

    private fun pointReady(notZeroPoint: Boolean, isDoubleClickOnRoute: Boolean, isSuitableType: Boolean) {
        if (!notZeroPoint) return
        if (isSuitableType || isDoubleClickOnRoute) {
            if (checkFields() && (isTo || orderInteractor.hourlyDuration != null)) {
                createRouteForOrder()
            } else if (!isTo) {
                viewState.setFocus(true)
                orderInteractor.to?.let {
                    viewState.setAddressTo(it.variants?.first ?: it.cityPoint.name, true, true)
                }
            }
        }
    }

    private fun checkPlaceType(address: GTAddress) = address.placeTypes.let { placeTypes ->
        when {
            placeTypes.isNullOrEmpty()      -> NO_TYPE
            placeTypes.contains(ROUTE_NAME) -> ROUTE_TYPE
            else                            -> SUITABLE_TYPE
        }
    }

    private fun checkFields(): Boolean {
        val isAddressesValid = orderInteractor.isAddressesValid()
        if (!isAddressesValid) return false
        val isDistanceValid = orderInteractor.hourlyDuration?.let { true } ?: orderInteractor.isDistanceFine()
        if (!isDistanceValid) viewState.setError(false, R.string.LNG_FIELD_ERROR_MATCH_ADDRESSES)
        return isDistanceValid
    }

    private fun createRouteForOrder() {
        fillHistory()
        viewState.goToCreateOrder()
        analytics.logSingleEvent(Analytics.REQUEST_FORM)
    }

    fun selectFinishPointOnMap() {
        worker.main.launch {
            withContext(worker.bg) {
                setSelectedField(if (isTo) FIELD_TO else FIELD_FROM)
                analytics.logSingleEvent(Analytics.POINT_ON_MAP_CLICKED)
            }
        }
        viewState.goToMap()
    }

    fun isHourly() = orderInteractor.hourlyDuration != null

    fun inverseWay() {
        analytics.logSingleEvent(Analytics.SWAP_CLICKED)
        isTo = !isTo
        val copyTo = orderInteractor.to
        orderInteractor.to = orderInteractor.from
        orderInteractor.from = copyTo
        viewState.setAddressFrom(orderInteractor.from?.cityPoint?.name ?: "", false, false)
        viewState.setAddressTo(orderInteractor.to?.cityPoint?.name ?: "", false, false)
        viewState.setFocus(isTo)
    }

    private fun checkZeroPoint(address: GTAddress): Boolean {
        val point = address.cityPoint.point!!
        return if (point.latitude == NO_POINT && point.longitude == NO_POINT) {
            orderInteractor.noPointPlaces += address
            if (isTo) orderInteractor.to else orderInteractor.from = null
            viewState.onAddressError(R.string.LNG_LOOKUP_ERROR, address, isTo)
            false
        } else {
            true
        }
    }

    private fun fillHistory() = worker.main.launch {
        withContext(worker.bg) {
            setAddressHistory(mutableListOf(orderInteractor.from!!).apply { orderInteractor.to?.let { add(it) } })
        }
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }

    companion object {
        const val ROUTE_NAME = "route"

        const val ROUTE_TYPE          = 1020
        const val STREET_ADDRESS_TYPE = 1021
        const val SUITABLE_TYPE       = 0
        const val NO_TYPE             = -1
        const val NO_POINT            = 0.0

        const val FIELD_FROM = "field_from"
        const val FIELD_TO = "field_to"
        const val EMPTY_ADDRESS = ""
    }
}
