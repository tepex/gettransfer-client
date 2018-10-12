package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.model.LatLngBounds
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.view.SearchAddressView
import ru.terrakok.cicerone.Router
import timber.log.Timber

@InjectViewState
class SearchAddressPresenter(cc: CoroutineContexts,
                             router: Router,
                             systemInteractor: SystemInteractor,
	                         private val routeInteractor: RouteInteractor): BasePresenter<SearchAddressView>(cc, router, systemInteractor) {
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

		utils.launchAsyncTryCatch({
			lastResult = utils.asyncAwait { routeInteractor.getAutocompletePredictions(prediction, latLonPair) }
			lastRequest = prediction
			viewState.setAddressList(lastResult!!)
		}, {e ->
			Timber.e(e)
			// if(e is ...) @TODO: обработать ошибки таймаута
			viewState.setError(false, R.string.err_address_service_xxx)
		})
	}

    fun onClearAddress(isTo: Boolean) {
        if(isTo) routeInteractor.to = null
        else routeInteractor.from = null
    }

    fun returnAddress(isTo: Boolean) {
        if(isTo) viewState.returnLastAddress(routeInteractor.to?.name ?: "")
        else viewState.returnLastAddress(routeInteractor.from?.name ?: "")
    }
}
