package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import android.util.Log

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.PopularPlace
import com.kg.gettransfer.presentation.view.SearchView

import ru.terrakok.cicerone.Router

@InjectViewState
class SearchPresenter(cc: CoroutineContexts,
                      router: Router,
                      systemInteractor: SystemInteractor,
                      private val routeInteractor: RouteInteractor): BasePresenter<SearchView>(cc, router, systemInteractor) {
    var isTo = false
    
    companion object {
        @JvmField val ADDRESS_PREDICTION_SIZE = 3

        const val ROUTE_TYPE          = 1020
        const val STREET_ADDRESS_TYPE = 1021
        const val SUITABLE_TYPE       = 0
        const val NO_TYPE             = -1
    }

    @CallSuper
    override fun attachView(view: SearchView) {
        super.attachView(view)
        viewState.setAddressFrom(routeInteractor.from?.cityPoint?.name ?: "", false, !isTo)
        viewState.setAddressTo(routeInteractor.to?.cityPoint?.name ?: "", false, isTo)
        onSearchFieldEmpty()
    }

    fun onSearchFieldEmpty() {
        viewState.setSuggestedAddresses(systemInteractor.getAddressHistory())
    }

    fun onPopularSelected(selected: PopularPlace) {
        viewState.onFindPopularPlace(isTo, selected.title)
    }
    
    fun onAddressSelected(selected: GTAddress) {
        val isDoubleClickOnRoute: Boolean
        if(isTo) {
            viewState.setAddressTo(selected.primary ?: selected.cityPoint.name!!, false, true)
            isDoubleClickOnRoute = routeInteractor.to == selected
            routeInteractor.to = selected
        } else {
            viewState.setAddressFrom(selected.primary ?: selected.cityPoint.name!!, false, true)
            isDoubleClickOnRoute = routeInteractor.from == selected
            routeInteractor.from = selected
        }

        val placeType = checkPlaceType(selected)
        if(placeType == SUITABLE_TYPE || (placeType == ROUTE_TYPE && isDoubleClickOnRoute)) {
            viewState.updateIcon(isTo)
            if(checkFields()) createRouteForOrder()
        } else {
            val sendRequest = selected.needApproximation() /* dirty hack */
            if(isTo) viewState.setAddressTo(selected.primary ?: selected.cityPoint.name!!, sendRequest, true)
            else viewState.setAddressFrom(selected.primary ?: selected.cityPoint.name!!, sendRequest, true)
        }
    }

    private fun checkPlaceType(address: GTAddress): Int {
        val placeTypes = address.placeTypes
        if(placeTypes == null || placeTypes.isEmpty()) return NO_TYPE
        if(placeTypes.contains(ROUTE_TYPE)) return ROUTE_TYPE
        return SUITABLE_TYPE
    }

    private fun checkFields() = routeInteractor.let { it.from != null && it.to != null && it.from != it.to }

    private fun createRouteForOrder() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
            utils.asyncAwait { routeInteractor.updateDestinationPoint() }
            utils.asyncAwait { routeInteractor.updateStartPoint() }
            systemInteractor.setAddressHistory(arrayListOf(routeInteractor.from!!,routeInteractor.to!!))

            router.navigateTo(Screens.CREATE_ORDER)
        }, { e ->
            viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    fun inverseWay() {
        val copyTo = routeInteractor.to
        routeInteractor.to = routeInteractor.from
        routeInteractor.from = copyTo
        viewState.setAddressFrom(routeInteractor.from?.cityPoint?.name ?: "", false, false)
        viewState.setAddressTo(routeInteractor.to?.cityPoint?.name ?: "", false, false)
    }
}
