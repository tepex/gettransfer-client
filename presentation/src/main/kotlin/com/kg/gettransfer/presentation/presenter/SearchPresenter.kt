package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.SearchView

import ru.terrakok.cicerone.Router

@InjectViewState
class SearchPresenter(cc: CoroutineContexts,
                      router: Router,
                      systemInteractor: SystemInteractor,
                      private val routeInteractor: RouteInteractor): BasePresenter<SearchView>(cc, router, systemInteractor) {
    var isTo = false

    companion object {
        @JvmField
        val ADDRESS_PREDICTION_SIZE = 3

        const val ROUTE_TYPE = 1020
        const val STREET_ADDRESS_TYPE = 1021
        const val SUITABLE_TYPE = 0
        const val NO_TYPE = -1
    }

    @CallSuper
    override fun attachView(view: SearchView) {
        super.attachView(view)
        viewState.setAddressFrom(routeInteractor.from!!.name, false)
        viewState.setAddressTo(routeInteractor.to?.name ?: "", false)
    }

    fun onAddressSelected(selected: GTAddress) {
        val isDoubleClickOnRoute: Boolean
        if(isTo) {
            isDoubleClickOnRoute = routeInteractor.to == selected
            routeInteractor.to = selected
        } else {
            isDoubleClickOnRoute = routeInteractor.from == selected
            routeInteractor.from = selected
        }

        val placeType = checkPlaceType(selected)
        if(placeType == SUITABLE_TYPE || (placeType == ROUTE_TYPE && isDoubleClickOnRoute)) {
            if(checkFields()) {
                utils.launchAsyncTryCatchFinally({
                    viewState.blockInterface(true)
                    utils.asyncAwait { routeInteractor.updateDestinationPoint() }
                    router.navigateTo(Screens.CREATE_ORDER)
                }, { e ->
                    viewState.setError(e)
                }, { viewState.blockInterface(false) })
            }
        } else {
            val sendRequest = selected.needApproximation() /* dirty hack */
            if(isTo) viewState.setAddressTo(selected.primary ?: selected.name, sendRequest)
            else viewState.setAddressFrom(selected.primary ?: selected.name, sendRequest)
        }
    }

    private fun checkPlaceType(address: GTAddress): Int {
        val placeTypes = address.placeTypes
        if(placeTypes == null || placeTypes.isEmpty()) return NO_TYPE
        if(placeTypes.contains(ROUTE_TYPE)) return ROUTE_TYPE
        return SUITABLE_TYPE
    }

    private fun checkFields() = routeInteractor.from != null && routeInteractor.to != null && routeInteractor.from != routeInteractor.to

    @CallSuper
    override fun onBackCommandClick() {
        routeInteractor.to = null
        super.onBackCommandClick()
    }
}
