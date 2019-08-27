package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_FROM
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_TO
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.EMPTY_ADDRESS

import com.kg.gettransfer.presentation.view.NewTransferMapView

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@InjectViewState
class NewTransferMapPresenter : BaseNewTransferPresenter<NewTransferMapView>() {

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null

    private var currentLocation: String = ""

    private var markerStateLifted = false
    var isMarkerAnimating = true

    private var idleAndMoveCamera = true

    private lateinit var selectedField: String

    override fun updateView() {
        fillViewFromState()
    }

    private fun fillViewFromState() {
        worker.main.launch {
            selectedField = getPreferences().getModel().selectedField
            viewState.initUIForSelectedField(selectedField)
            initAddressFieldAndMarker(selectedField)
            if (fillAddressFieldsCheckIsEmpty()) {
                updateCurrentLocation(isFromFieldSelected())
            }
        }
    }

    private fun initAddressFieldAndMarker(field: String) {
        val pointSelectedField: Point? = when (field) {
            FIELD_FROM -> orderInteractor.from?.cityPoint?.point
            FIELD_TO   -> orderInteractor.to?.cityPoint?.point
            else       -> null
        }

        val latLngPointSelectedField: LatLng? = pointSelectedField?.let { LatLng(it.latitude, it.longitude) }

        latLngPointSelectedField?.let { point ->
            idleAndMoveCamera = false
            viewState.setMapPoint(point, false, showBtnMyLocation(point))
        }
    }

    override fun fillAddressFieldsCheckIsEmpty(): Boolean {
        with(orderInteractor) {
            val address = (if (selectedField == FIELD_FROM) from else to)?.address
            viewState.setAddress(address ?: EMPTY_ADDRESS)
            return address == null
        }
    }

    override fun updateCurrentLocationAsync(isFromField: Boolean) {
        viewState.blockAddressField()
        super.updateCurrentLocationAsync(isFromField)
    }

    override fun setPointAddress(currentAddress: GTAddress) {
        super.setPointAddress(currentAddress)

        lastAddressPoint = pointMapper.toLatLng(currentAddress.cityPoint.point!!)
        onCameraMove(lastAddressPoint, !comparePointsWithRounding(lastAddressPoint, lastPoint))
        viewState.setMapPoint(lastAddressPoint, true, showBtnMyLocation(lastAddressPoint))
        viewState.setAddress(currentAddress.cityPoint.name)
    }

    private fun showBtnMyLocation(point: LatLng) = lastCurrentLocation == null || point != lastCurrentLocation

    fun onCameraMove(lastPoint: LatLng, animateMarker: Boolean) {
        if (idleAndMoveCamera) {
            if (!markerStateLifted && !isMarkerAnimating && animateMarker) {
                viewState.setMarkerElevation(true)
                markerStateLifted = true
            }
            this.lastPoint = lastPoint
            viewState.moveCenterMarker(lastPoint)
            worker.main.launch {
                viewState.blockAddressField()
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onCameraIdle(latLngBounds: LatLngBounds) {
        if (idleAndMoveCamera) {
            if (markerStateLifted) {
                viewState.setMarkerElevation(false)
                markerStateLifted = false
            }
            if (lastPoint == null) {
                return
            }

            lastAddressPoint = lastPoint!!

            worker.main.launch {
                withContext(worker.bg) {
                    orderInteractor.getAddressByLocation(
                        getPreferences().getModel().selectedField == FIELD_FROM,
                        pointMapper.fromLatLng(lastPoint!!)
                    )
                }.isSuccess()?.let { addr ->
                    currentLocation = addr.cityPoint.name
                    viewState.setAddress(currentLocation)
                }
            }
        } else {
            idleAndMoveCamera = true
            fillAddressFieldsCheckIsEmpty()
        }
    }

    fun navigateToFindAddress() {
        viewState.goToSearchAddress(selectedField == FIELD_TO, true)
    }

    fun enablePinAnimation() {
        isMarkerAnimating = false
    }

    fun updateLocation() {
        updateCurrentLocation(isFromFieldSelected())
    }

    private fun isFromFieldSelected() = selectedField == FIELD_FROM

    private fun comparePointsWithRounding(point1: LatLng?, point2: LatLng?): Boolean {
        if (point2 == null || point1 == null) {
            return false
        }
        val criteria = 0.000_001

        var latDiff = point1.latitude - point1.latitude
        if (latDiff < 0) latDiff *= -1
        var lngDiff = point2.longitude - point2.longitude
        if (lngDiff < 0) lngDiff *= -1
        return latDiff < criteria && lngDiff < criteria
    }
}
