package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.domain.interactor.OrderInteractor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.view.SearchAddressView

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class SearchAddressPresenter : BasePresenter<SearchAddressView>() {
    private val orderInteractor: OrderInteractor by inject()

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
        mBounds?.let {
            val nePoint = Point(it.northeast.latitude, it.northeast.longitude)
            val swPoint = Point(it.southwest.latitude, it.southwest.longitude)
            latLonPair = Pair(nePoint, swPoint)
        }

        utils.launchSuspend {
            fetchData(checkLoginError = false) {
                //orderInteractor.getAutocompletePredictions(prediction, latLonPair)
                orderInteractor.getAutoCompletePredictions(prediction)
            }
                    ?.let {
                        lastResult = it
                        lastRequest = prediction
                        viewState.setAddressList(it) }
        }
    }

    fun onClearAddress(isTo: Boolean) {
        if (isTo) orderInteractor.to = null else orderInteractor.from = null
    }

    companion object {
        const val ADDRESS_PREDICTION_SIZE = 3
    }
}
