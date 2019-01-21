package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.model.PopularPlace

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SearchView

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

@InjectViewState
class SearchPresenter : BasePresenter<SearchView>() {
    private val routeInteractor: RouteInteractor by inject()

    internal var isTo = false

    @CallSuper
    override fun attachView(view: SearchView) {
        super.attachView(view)
        viewState.setAddressFrom(routeInteractor.from?.cityPoint?.name ?: "", false, !isTo)
        if (routeInteractor.hourlyDuration == null)
            viewState.setAddressTo(routeInteractor.to?.cityPoint?.name ?: "", false, isTo)
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
        val isDoubleClickOnRoute: Boolean
        if (isTo) {
            viewState.setAddressTo(selected.primary ?: selected.cityPoint.name!!, false, true)
            isDoubleClickOnRoute = routeInteractor.to == selected
            routeInteractor.to = selected
        } else {
            viewState.setAddressFrom(selected.primary ?: selected.cityPoint.name!!, false, true)
            isDoubleClickOnRoute = routeInteractor.from == selected
            routeInteractor.from = selected
        }

        val placeType = checkPlaceType(selected)
        if (placeType == SUITABLE_TYPE || (placeType == ROUTE_TYPE && isDoubleClickOnRoute)) {
            viewState.updateIcon(isTo)
            utils.launchSuspend {
                utils.async { routeInteractor.updatePoint(isTo) }
                        .await()
                        .model.also {
                    pointReady(checkZeroPoint(it, selected)) }

            }
        } else {
            val sendRequest = selected.needApproximation() /* dirty hack */
            if (isTo) viewState.setAddressTo(selected.primary ?: selected.cityPoint.name!!, sendRequest, true)
            else viewState.setAddressFrom(selected.primary ?: selected.cityPoint.name!!, sendRequest, true)
        }
    }

    private fun pointReady(notZeroPoint: Boolean) {
        if (!notZeroPoint) return
        if (checkFields() && isTo) createRouteForOrder()
        else if (!isTo) {
            viewState.setFocus(true)
            routeInteractor.to?.let {
                viewState.setAddressTo(it.primary ?: it.cityPoint.name!!, true , true)
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

    private fun checkFields() = routeInteractor.addressFieldsNotNull()

    private fun createRouteForOrder() {
        systemInteractor.addressHistory = listOf(routeInteractor.from!!, routeInteractor.to!!)
        router.replaceScreen(Screens.CreateOrder)
        logButtons(Analytics.REQUEST_FORM)
    }

    fun selectFinishPointOnMap() {
        logButtons(Analytics.POINT_ON_MAP_CLICKED)
        systemInteractor.selectedField = if (isTo) MainPresenter.FIELD_TO else MainPresenter.FIELD_FROM
        router.exit()
    }

    private fun logButtons(event: String) {
        analytics.logEventToFirebase(event, null)
        analytics.logEventToYandex(event, null)
    }

    fun isHourly() = routeInteractor.hourlyDuration != null

    @CallSuper
    override fun onBackCommandClick() {
        super.onBackCommandClick()
    }

    fun inverseWay() {
        logButtons(Analytics.SWAP_CLICKED)
        isTo = !isTo
        val copyTo = routeInteractor.to
        routeInteractor.to = routeInteractor.from
        routeInteractor.from = copyTo
        viewState.setAddressFrom(routeInteractor.from?.cityPoint?.name ?: "", false, false)
        viewState.setAddressTo(routeInteractor.to?.cityPoint?.name ?: "", false, false)
        viewState.setFocus(isTo)
    }

    private fun checkZeroPoint(point: Point, address: GTAddress): Boolean {
        if (point.latitude == NO_POINT && point.longitude == NO_POINT) {
            routeInteractor.noPointPlaces += address
            if (isTo) routeInteractor.to else routeInteractor.from = null
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
