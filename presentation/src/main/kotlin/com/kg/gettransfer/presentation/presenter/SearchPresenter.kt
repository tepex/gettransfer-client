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
    var popularPlaceIcons: ArrayList<Int>? = null
    var popularPlaceTitles: ArrayList<String>? = null
    val popularSize = 3

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
        viewState.setAddressFrom(routeInteractor.from!!.cityPoint.name!!, false)
        viewState.setAddressTo(routeInteractor.to?.cityPoint?.name ?: "", false)
        showSuggestions()
    }

    fun onPopularSelected(selected: PopularPlace){
        viewState.onFindPopularPlace(isTo, selected.title)
    }

    fun onSearchFieldEmpty() = showSuggestions()

    private fun showSuggestions() {
        val lastPlaces = getLastAddressesList()
        val popularPlaces = createPopularList()
        viewState.setSuggestedAddresses(lastPlaces, popularPlaces)
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
                    utils.asyncAwait { routeInteractor.updateStartPoint() }
                    systemInteractor.setAddressHistory(arrayListOf(routeInteractor.from!!,routeInteractor.to!!))

                    router.navigateTo(Screens.CREATE_ORDER)
                }, { e ->
                    viewState.setError(e)
                }, { viewState.blockInterface(false) })
            }
        } else {
            val sendRequest = selected.needApproximation() /* dirty hack */
            if(isTo) viewState.setAddressTo(selected.primary ?: selected.cityPoint.name!!, sendRequest)
            else viewState.setAddressFrom(selected.primary ?: selected.cityPoint.name!!, sendRequest)
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
        super.onBackCommandClick()
    }

    fun inverseWay() {
        val copyTo = routeInteractor.to
        routeInteractor.to = routeInteractor.from
        routeInteractor.from = copyTo
        viewState.setAddressFrom(routeInteractor.from?.cityPoint?.name ?: "", false)
        viewState.setAddressTo(routeInteractor.to?.cityPoint?.name ?: "", false)
    }

    private fun createPopularList():List<PopularPlace>{
        val list = ArrayList<PopularPlace>()
        for (i in 0 until popularSize) list.add(PopularPlace(popularPlaceTitles!![i], popularPlaceIcons!![i]))
        return list
    }

    private fun getLastAddressesList() = systemInteractor.getAddressHistory()
}
