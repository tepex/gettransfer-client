package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.view.NewTransferMapView

import org.koin.core.KoinComponent

@InjectViewState
class NewTransferMapPresenter : BaseNewTransferPresenter<NewTransferMapView>(), KoinComponent {

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null

    private var currentLocation: String = ""

    private var markerStateLifted = false
    var isMarkerAnimating = true

    private var idleAndMoveCamera = true

    override fun updateView(isVisibleView: Boolean) {
        resetState()
        if (!isVisibleView) return

        if (nState.isSwitchToMain) {//returned from create order
            viewState.switchToMain()
            return
        }
        fillViewFromState()
    }

    fun resetState() {
        nState.reset()
    }

    override fun changeUsedField(field: String) {
        super.changeUsedField(field)

        val pointSelectedField: Point? = when (field) {
            FIELD_FROM -> orderInteractor.from?.cityPoint?.point
            FIELD_TO -> orderInteractor.to?.cityPoint?.point
            else -> null
        }
        var latLngPointSelectedField: LatLng? = null
        if (pointSelectedField != null) latLngPointSelectedField = LatLng(pointSelectedField.latitude, pointSelectedField.longitude)
        when (systemInteractor.selectedField) {
            FIELD_FROM -> viewState.selectFieldFrom()
            FIELD_TO -> viewState.setFieldTo()
        }
        if (latLngPointSelectedField != null) {
            idleAndMoveCamera = false
            viewState.setMapPoint(latLngPointSelectedField, false, showBtnMyLocation(latLngPointSelectedField))
        }
    }

    override fun setPointAddress(currentAddress: GTAddress) {
        super.setPointAddress(currentAddress)

        lastAddressPoint = pointMapper.toLatLng(currentAddress.cityPoint.point!!)
        onCameraMove(lastAddressPoint, !comparePointsWithRounding(lastAddressPoint, lastPoint))
        viewState.setMapPoint(lastAddressPoint, true, showBtnMyLocation(lastAddressPoint))
        setAddressInSelectedField(currentAddress.cityPoint.name)

        lastAddressPoint = pointMapper.toLatLng(currentAddress.cityPoint.point!!)
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
            blockSelectedField(systemInteractor.selectedField)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onCameraIdle(latLngBounds: LatLngBounds) {
        if (idleAndMoveCamera) {
            if (markerStateLifted) {
                viewState.setMarkerElevation(false)
                markerStateLifted = false
            }
            if (lastPoint == null) return

            lastAddressPoint = lastPoint!!

            utils.launchSuspend {
                utils.asyncAwait {
                    orderInteractor.getAddressByLocation(systemInteractor.selectedField == FIELD_FROM,
                            pointMapper.fromLatLng(lastPoint!!))
                }
                        .isSuccess()
                        ?.let {
                            currentLocation = it.cityPoint.name
                            setAddressInSelectedField(currentLocation)
                        }
            }
        } else {
            idleAndMoveCamera = true
            fillAddressFieldsCheckIsEmpty()
        }
    }

    fun enablePinAnimation() {
        isMarkerAnimating = false
    }

    private fun comparePointsWithRounding(point1: LatLng?, point2: LatLng?): Boolean {
        if (point2 == null || point1 == null) return false
        val criteria = 0.000_001

        var latDiff = point1.latitude - point1.latitude
        if (latDiff < 0) latDiff *= -1
        var lngDiff = point2.longitude - point2.longitude
        if (lngDiff < 0) lngDiff *= -1
        return latDiff < criteria && lngDiff < criteria
    }

    companion object {
        const val FIELD_FROM = "field_from"
        const val FIELD_TO = "field_to"
    }
}
