package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.RouteInteractor

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
        logEvent(Analytics.PREDEFINED_CLICKED + selected.title.toLowerCase())
        viewState.onFindPopularPlace(isTo, selected.title)
    }

    fun onAddressSelected(selected: GTAddress) {
        logEvent(Analytics.LAST_PLACE_CLICKED)
        val isDoubleClickOnRoute: Boolean
        if (isTo) {
            viewState.setAddressTo(selected.primary ?: selected.cityPoint.name!!, false, true)
            isDoubleClickOnRoute = routeInteractor.to == selected
            routeInteractor.to = selected
            utils.launchSuspend { utils.asyncAwait { routeInteractor.updateDestinationPoint() }}
        } else {
            viewState.setAddressFrom(selected.primary ?: selected.cityPoint.name!!, false, true)
            isDoubleClickOnRoute = routeInteractor.from == selected
            routeInteractor.from = selected
            utils.launchSuspend { utils.asyncAwait { routeInteractor.updateStartPoint() }}
        }

        val placeType = checkPlaceType(selected)
        if (placeType == SUITABLE_TYPE || (placeType == ROUTE_TYPE && isDoubleClickOnRoute)) {
            viewState.updateIcon(isTo)
            if (checkFields() && isTo) createRouteForOrder()
            else if (!isTo) {
                viewState.changeFocusToDestField()
                routeInteractor.to?.let {
                    viewState.setAddressTo(it.primary ?: it.cityPoint.name!!, true , true)
                }
            }
        } else {
            val sendRequest = selected.needApproximation() /* dirty hack */
            if (isTo) viewState.setAddressTo(selected.primary ?: selected.cityPoint.name!!, sendRequest, true)
            else viewState.setAddressFrom(selected.primary ?: selected.cityPoint.name!!, sendRequest, true)
        }
    }

    private fun checkPlaceType(address: GTAddress): Int {
        val placeTypes = address.placeTypes
        if (placeTypes == null || placeTypes.isEmpty()) return NO_TYPE
        if (placeTypes.contains(ROUTE_TYPE)) return ROUTE_TYPE
        return SUITABLE_TYPE
    }

    private fun checkFields() = routeInteractor.addressFieldsNotNull()

    private fun createRouteForOrder() {
        systemInteractor.addressHistory = listOf(routeInteractor.from!!, routeInteractor.to!!)
        router.replaceScreen(Screens.CreateOrder)
        logEvent(Analytics.REQUEST_FORM)
    }

    fun selectFinishPointOnMap() {
        logEvent(Analytics.POINT_ON_MAP_CLICKED)
        systemInteractor.selectedField = if (isTo) MainPresenter.FIELD_TO else MainPresenter.FIELD_FROM
        router.exit()
    }

    private fun logEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_NAME] = value

        analytics.logEvent(Analytics.EVENT_BUTTONS, createStringBundle(Analytics.PARAM_KEY_NAME, value), map)
    }

    @CallSuper
    override fun onBackCommandClick() {
        super.onBackCommandClick()
    }

    fun inverseWay() {
        logEvent(Analytics.SWAP_CLICKED)
        isTo = !isTo
        val copyTo = routeInteractor.to
        routeInteractor.to = routeInteractor.from
        routeInteractor.from = copyTo
        viewState.setAddressFrom(routeInteractor.from?.cityPoint?.name ?: "", false, false)
        viewState.setAddressTo(routeInteractor.to?.cityPoint?.name ?: "", false, false)
        viewState.setFocus(isTo)
    }

    companion object {
        const val ADDRESS_PREDICTION_SIZE = 3

        const val ROUTE_TYPE          = 1020
        const val STREET_ADDRESS_TYPE = 1021
        const val SUITABLE_TYPE       = 0
        const val NO_TYPE             = -1
    }
}
