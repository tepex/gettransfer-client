package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.view.SearchAddressView

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class SearchAddressPresenter: BasePresenter<SearchAddressView>() {
    private val routeInteractor: RouteInteractor by inject()
	/* Cache. @TODO */
	private var lastRequest: String? = null
	private var lastResult: List<GTAddress>? = null
	internal var mBounds: LatLngBounds? = null
	
	companion object {
		@JvmField val ADDRESS_PREDICTION_SIZE = 3
	}
	
	fun requestAddressListByPrediction(prediction: String) {
		if(prediction.length < ADDRESS_PREDICTION_SIZE) {
			viewState.setAddressList(emptyList())
			return
		}
		if(prediction == lastRequest && lastResult != null) {
			Timber.d("------ From cache $lastRequest")
			viewState.setAddressList(lastResult!!)
			return
		}
			
		Timber.d("------ request list for prediction $prediction")
		var latLonPair: Pair<Point, Point>? = null
		if(mBounds != null) {
			val nePoint = Point(mBounds!!.northeast.latitude, mBounds!!.northeast.longitude)
			val swPoint = Point(mBounds!!.southwest.latitude, mBounds!!.southwest.longitude)
			latLonPair = Pair(nePoint, swPoint)
		}

		utils.launchSuspend {
		    val result = utils.asyncAwait { routeInteractor.getAutocompletePredictions(prediction, latLonPair) }
		    if(result.error != null) {
		        Timber.e(result.error!!)
		        viewState.setError(result.error!!)
		    } else {
		        lastResult = result.model
		        lastRequest = prediction
		        viewState.setAddressList(result.model)
		    }
		}
	}

    fun onClearAddress(isTo: Boolean) {
        if(isTo) routeInteractor.to = null
        else routeInteractor.from = null
    }

    fun returnAddress(isTo: Boolean) {
        if(isTo) viewState.returnLastAddress(routeInteractor.to?.cityPoint?.name ?: "")
        else viewState.returnLastAddress(routeInteractor.from?.cityPoint?.name ?: "")
    }
}
