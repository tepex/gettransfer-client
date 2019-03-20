package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.view.SearchAddressView

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class SearchAddressPresenter : BasePresenter<SearchAddressView>() {
    private val routeInteractor: RouteInteractor by inject()

    /* Cache. @TODO */
    private var lastRequest: String? = null
    private var lastResult: List<GTAddress>? = null
    internal var mBounds: LatLngBounds? = null

    fun requestAddressListByPrediction(prediction: String) {
        if (prediction.length < ADDRESS_PREDICTION_SIZE) {
            viewState.setAddressList(emptyList())
            return
        }
        if (prediction == lastRequest && lastResult != null) {
            Timber.d("------ From cache $lastRequest")
            viewState.setAddressList(lastResult!!)
            return
        }

        Timber.d("------ request list for prediction $prediction")
        var latLonPair: Pair<Point, Point>? = null
        if (mBounds != null) {
            val nePoint = Point(mBounds!!.northeast.latitude, mBounds!!.northeast.longitude)
            val swPoint = Point(mBounds!!.southwest.latitude, mBounds!!.southwest.longitude)
            latLonPair = Pair(nePoint, swPoint)
        }

        utils.launchSuspend {
            fetchData(checkLoginError = false){ routeInteractor.getAutocompletePredictions(prediction, latLonPair) }
                    ?.let {
                        lastResult = it
                        lastRequest = prediction
                        viewState.setAddressList(it) }
        }
    }

    fun onClearAddress(isTo: Boolean) {
        if (isTo) routeInteractor.to = null else routeInteractor.from = null
    }

    fun returnAddress(isTo: Boolean) {
        viewState.returnLastAddress(
            if (isTo) routeInteractor.to?.cityPoint?.name ?: "" else routeInteractor.from?.cityPoint?.name ?: ""
        )
    }

    companion object {
        const val ADDRESS_PREDICTION_SIZE = 3
    }
}
