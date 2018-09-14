package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.SearchView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class SearchPresenter(cc: CoroutineContexts,
	                  router: Router,
	                  apiInteractor: ApiInteractor,
	                  private val addressInteractor: AddressInteractor): BasePresenter<SearchView>(cc, router, apiInteractor) {
	var isTo = false
	
	private lateinit var addressFrom: GTAddress
	private var addressTo: GTAddress? = null
	
	companion object {
		@JvmField val ADDRESS_PREDICTION_SIZE = 3
	}
	
    override fun onFirstViewAttach() {
        addressFrom = addressInteractor.getCachedAddress()!!
	}

    @CallSuper
    override fun attachView(view: SearchView) {
        super.attachView(view)
        viewState.setAddressFrom(addressFrom.name, false)
        viewState.setAddressTo(addressTo?.name ?: "", false)
    }
    
    fun onAddressSelected(selected: GTAddress) {
        if(isTo) addressTo = selected else addressFrom = selected
        
        if(addressFrom.isConcreteObject() && addressTo?.isConcreteObject() ?: false) createRoute()
        else {
            val sendRequest = selected.needApproximation() /* dirty hack */
            if(isTo) viewState.setAddressTo(selected.primary ?: selected.name, sendRequest)
            else viewState.setAddressFrom(selected.primary ?: selected.name, sendRequest)
        }
    }
    
    private fun createRoute() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
	        if(addressTo!!.point == null)
	            addressTo!!.point = utils.asyncAwait { addressInteractor.getLatLngByPlaceId(addressTo!!.id!!) }
	        addressInteractor.route = Pair(addressFrom, addressTo!!)
	        routeInfo = apiInteractor.getRouteInfo(arrayOf(addressInteractor.route.first.point.toString(),
                                                               secondPoint.toString()), true, false)
	        router.navigateTo(Screens.CREATE_ORDER)
	    }, { _ -> viewState.setError(false, R.string.err_address_service_xxx)
        }, { viewState.blockInterface(false) })
	}
}
