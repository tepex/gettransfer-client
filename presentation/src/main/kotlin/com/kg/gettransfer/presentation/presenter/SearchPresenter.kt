package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.OrderInteractor

import com.kg.gettransfer.presentation.model.PopularPlace

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SearchView

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.MainState

import org.koin.core.inject

@InjectViewState
class SearchPresenter : BasePresenter<SearchView>() {
    private val orderInteractor: OrderInteractor by inject()
    private val nState: MainState by inject()

    internal var isTo = false
    var backwards: Boolean = false

    @CallSuper
    override fun attachView(view: SearchView) {
        super.attachView(view)
        viewState.setAddressFrom(orderInteractor.from?.cityPoint?.name ?: "", true, !isTo)
        if (orderInteractor.hourlyDuration == null)
            viewState.setAddressTo(orderInteractor.to?.cityPoint?.name ?: "", true, isTo)
        else viewState.hideAddressTo()
        onSearchFieldEmpty()
    }

    fun onSearchFieldEmpty() = viewState.setSuggestedAddresses(systemInteractor.addressHistory)

    fun onPopularSelected(selected: PopularPlace) {
        analytics.logSingleEvent(Analytics.PREDEFINED_CLICKED + selected.title.toLowerCase())
        viewState.onFindPopularPlace(isTo, selected.title)
    }

    fun onAddressSelected(selected: GTAddress) {
        utils.launchSuspend {
            val oldAddress = if (isTo) orderInteractor.to else orderInteractor.from //for detecting double click
            var updatedGTAddress: GTAddress? = null //address with point
            selected.cityPoint.placeId?.let { placeId ->
                fetchResult { orderInteractor.updatePoint(isTo, placeId) }
                        .isSuccess()
                        ?.let {
                            updatedGTAddress = it
                        }
            }
            val newAddress = updatedGTAddress ?: selected
            analytics.logSingleEvent(Analytics.LAST_PLACE_CLICKED)
            val placeType = checkPlaceType(newAddress)
            val isDoubleClickOnRoute =
                    if (oldAddress?.cityPoint?.point != null) oldAddress.cityPoint.point == updatedGTAddress?.cityPoint?.point else false
            if (isTo) {
                viewState.setAddressTo(newAddress.variants?.first
                        ?: newAddress.cityPoint.name, placeType == ROUTE_TYPE, true)
                orderInteractor.to = newAddress
            } else {
                viewState.setAddressFrom(newAddress.variants?.first
                        ?: newAddress.cityPoint.name, placeType == ROUTE_TYPE, true)
                orderInteractor.from = newAddress
            }

            if (placeType != ROUTE_TYPE || isDoubleClickOnRoute) {
                viewState.updateIcon(isTo)
                if (newAddress.cityPoint.point != null) {
                    pointReady(checkZeroPoint(newAddress), isDoubleClickOnRoute, true)
                } else {
                    if (selected.cityPoint.placeId != null) {
                        fetchResult { orderInteractor.updatePoint(isTo, selected.cityPoint.placeId!!) }
                                .isSuccess()
                                ?.let {
                                    pointReady(checkZeroPoint(it), isDoubleClickOnRoute, true)
                                }
                    }
                }
            } else {
                val sendRequest = newAddress.needApproximation() /* dirty hack */

                if (isTo) viewState.setAddressTo(newAddress.variants?.first ?: newAddress.cityPoint.name, sendRequest, true)
                else viewState.setAddressFrom(newAddress.variants?.first ?: newAddress.cityPoint.name, sendRequest, true)
            }
        }
    }

    private fun pointReady(notZeroPoint: Boolean, isDoubleClickOnRoute: Boolean, isSuitableType: Boolean) {
        if (!notZeroPoint) return
        if(isSuitableType || isDoubleClickOnRoute) {
            if (checkFields() && (isTo || orderInteractor.hourlyDuration != null)) createRouteForOrder()
            else if (!isTo) {
                viewState.setFocus(true)
                orderInteractor.to?.let {
                    viewState.setAddressTo(it.variants?.first ?: it.cityPoint.name, true, true)
                }
            }
        }
    }

    private fun checkPlaceType(address: GTAddress) =
            address.placeTypes.let {
                when {
                    it.isNullOrEmpty()      -> NO_TYPE
                    it.contains(ROUTE_NAME) -> ROUTE_TYPE
                    else                    -> SUITABLE_TYPE
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
        if (backwards) router.exit()
        else router.replaceScreen(Screens.CreateOrder)
        analytics.logSingleEvent(Analytics.REQUEST_FORM)
    }

    fun selectFinishPointOnMap() {
        analytics.logSingleEvent(Analytics.POINT_ON_MAP_CLICKED)
        systemInteractor.selectedField = if (isTo) MainPresenter.FIELD_TO else MainPresenter.FIELD_FROM
        nState.currentState = MainState.CHOOSE_POINT_ON_MAP
        router.exit()
    }

    fun isHourly() = orderInteractor.hourlyDuration != null

    @CallSuper
    override fun onBackCommandClick() {
        super.onBackCommandClick()
    }

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
        if (point.latitude == NO_POINT && point.longitude == NO_POINT) {
            orderInteractor.noPointPlaces += address
            if (isTo) orderInteractor.to else orderInteractor.from = null
            viewState.onAddressError(R.string.LNG_LOOKUP_ERROR, address, isTo)
            return false
        }
        return true
    }

    private fun fillHistory() {
        systemInteractor.addressHistory = mutableListOf(orderInteractor.from!!)
                .apply { orderInteractor.to?.let { add(it) } }
    }


    companion object {
        const val ROUTE_NAME = "route"

        const val ADDRESS_PREDICTION_SIZE = 3

        const val ROUTE_TYPE          = 1020
        const val STREET_ADDRESS_TYPE = 1021
        const val SUITABLE_TYPE       = 0
        const val NO_TYPE             = -1
        const val NO_POINT            = 0.0
    }
}
