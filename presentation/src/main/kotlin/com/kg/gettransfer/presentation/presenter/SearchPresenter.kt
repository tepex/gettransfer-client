package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.model.PopularPlace

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SearchView

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

@InjectViewState
class SearchPresenter : BasePresenter<SearchView>() {
    private val orderInteractor: OrderInteractor by inject()

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
        logButtons(Analytics.PREDEFINED_CLICKED + selected.title.toLowerCase())
        viewState.onFindPopularPlace(isTo, selected.title)
    }

    fun onAddressSelected(selected: GTAddress) {
        logButtons(Analytics.LAST_PLACE_CLICKED)
        val placeType = checkPlaceType(selected)
        val isDoubleClickOnRoute: Boolean
        if (isTo) {
            viewState.setAddressTo(selected.primary ?: selected.cityPoint.name!!, placeType == ROUTE_TYPE, true)
            isDoubleClickOnRoute = orderInteractor.to == selected
            orderInteractor.to = selected
        } else {
            viewState.setAddressFrom(selected.primary ?: selected.cityPoint.name!!, placeType == ROUTE_TYPE, true)
            isDoubleClickOnRoute = orderInteractor.from == selected
            orderInteractor.from = selected
        }

        if (placeType != NO_TYPE) {
            viewState.updateIcon(isTo)
            utils.launchSuspend {
                utils.async { orderInteractor.updatePoint(isTo) }
                        .await()
                        .model.also {
                    pointReady(checkZeroPoint(it, selected), isDoubleClickOnRoute, placeType == SUITABLE_TYPE) }

            }
        } else {
            val sendRequest = selected.needApproximation() /* dirty hack */
            if (isTo) viewState.setAddressTo(selected.primary ?: selected.cityPoint.name!!, sendRequest, true)
            else viewState.setAddressFrom(selected.primary ?: selected.cityPoint.name!!, sendRequest, true)
        }
    }

    private fun pointReady(notZeroPoint: Boolean, isDoubleClickOnRoute: Boolean, isSuitableType: Boolean) {
        if (!notZeroPoint) return
        if(isSuitableType || isDoubleClickOnRoute) {
            if (checkFields() && isTo) createRouteForOrder()
            else if (!isTo) {
                viewState.setFocus(true)
                orderInteractor.to?.let {
                    viewState.setAddressTo(it.primary ?: it.cityPoint.name!!, true, true)
                }
            }
        }
    }

    private fun checkPlaceType(address: GTAddress) =
            address.placeTypes.let {
                when {
                    it.isNullOrEmpty()       -> NO_TYPE
                    it.contains(ROUTE_TYPE)  -> ROUTE_TYPE
                    else                     -> SUITABLE_TYPE
                }
            }

    private fun checkFields() =
        orderInteractor.isAddressesValid {
            viewState.setError(false, R.string.LNG_FIELD_ERROR_MATCH_ADDRESSES)
            it
        }


    private fun createRouteForOrder() {
        systemInteractor.addressHistory = listOf(orderInteractor.from!!, orderInteractor.to!!)
        if (backwards) router.exit()
        else router.replaceScreen(Screens.CreateOrder)
        logButtons(Analytics.REQUEST_FORM)
    }

    fun selectFinishPointOnMap() {
        logButtons(Analytics.POINT_ON_MAP_CLICKED)
        systemInteractor.selectedField = if (isTo) MainPresenter.FIELD_TO else MainPresenter.FIELD_FROM
        systemInteractor.withPointOnMap = true
        router.exit()
    }

    private fun logButtons(event: String) {
        analytics.logEventToFirebase(event, null)
        analytics.logEventToYandex(event, null)
    }

    fun isHourly() = orderInteractor.hourlyDuration != null

    @CallSuper
    override fun onBackCommandClick() {
        super.onBackCommandClick()
    }

    fun inverseWay() {
        logButtons(Analytics.SWAP_CLICKED)
        isTo = !isTo
        val copyTo = orderInteractor.to
        orderInteractor.to = orderInteractor.from
        orderInteractor.from = copyTo
        viewState.setAddressFrom(orderInteractor.from?.cityPoint?.name ?: "", false, false)
        viewState.setAddressTo(orderInteractor.to?.cityPoint?.name ?: "", false, false)
        viewState.setFocus(isTo)
    }

    private fun checkZeroPoint(point: Point, address: GTAddress): Boolean {
        if (point.latitude == NO_POINT && point.longitude == NO_POINT) {
            orderInteractor.noPointPlaces += address
            if (isTo) orderInteractor.to else orderInteractor.from = null
            viewState.onAddressError(R.string.LNG_LOOKUP_ERROR, address, isTo)
            return false
        }
        return true
    }

    companion object {
        const val ADDRESS_PREDICTION_SIZE = 3

        const val ROUTE_TYPE          = 1020
        const val STREET_ADDRESS_TYPE = 1021
        const val SUITABLE_TYPE       = 0
        const val NO_TYPE             = -1
        const val NO_POINT            = 0.0
    }
}
