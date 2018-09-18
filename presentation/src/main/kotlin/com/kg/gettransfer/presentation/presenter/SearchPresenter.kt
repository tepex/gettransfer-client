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
		@JvmField val ADDRESS_PREDICTION_SIZE = 3
	}
	
    @CallSuper
    override fun attachView(view: SearchView) {
        super.attachView(view)
        viewState.setAddressFrom(routeInteractor.from.name, false)
        viewState.setAddressTo(routeInteractor.to?.name ?: "", false)
    }
    
    fun onAddressSelected(selected: GTAddress) {
        if(isTo) routeInteractor.to = selected else routeInteractor.from = selected
        
        if(routeInteractor.isConcreteObjects()) {
            utils.launchAsyncTryCatchFinally({
                viewState.blockInterface(true)
                utils.asyncAwait { routeInteractor.updateDestinationPoint() }
                router.navigateTo(Screens.CREATE_ORDER)
            }, { e -> viewState.setError(false, R.string.err_server, e.message)
            }, { viewState.blockInterface(false) })
        }
        else {
            val sendRequest = selected.needApproximation() /* dirty hack */
            if(isTo) viewState.setAddressTo(selected.primary ?: selected.name, sendRequest)
            else viewState.setAddressFrom(selected.primary ?: selected.name, sendRequest)
        }
    }
    
    @CallSuper
    override fun onBackCommandClick() {
        routeInteractor.to = null
        super.onBackCommandClick()
    }
}
