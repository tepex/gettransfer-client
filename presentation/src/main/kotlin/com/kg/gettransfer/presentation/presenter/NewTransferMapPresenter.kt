package com.kg.gettransfer.presentation.presenter

import androidx.fragment.app.Fragment

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.core.domain.GTAddress
import com.kg.gettransfer.core.domain.Point

import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_FROM
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_TO
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.EMPTY_ADDRESS

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.NewTransferMapView
import com.kg.gettransfer.utilities.LocationManager

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import moxy.InjectViewState

@InjectViewState
class NewTransferMapPresenter : BaseNewTransferPresenter<NewTransferMapView>() {

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null

    private var currentLocation: String = ""

    private var markerStateLifted = false
    var isMarkerAnimating = true

    private var idleAndMoveCamera = true

    private var selectedField = FIELD_FROM

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (sessionInteractor.isAppLanguageChanged) {
           viewState.showRestartDialog()
        }
        initAddressListener()
        initEmptyAddressListener()
    }

    private fun initEmptyAddressListener() {
        locationManager.emptyAddressListener = object : LocationManager.OnGetEmptyAddressListener {
            override fun onGetEmptyAddress() {
                viewState.setAddress(EMPTY_ADDRESS)
            }
        }
    }

    private fun initAddressListener() {
        locationManager.addressListener = object : LocationManager.OnGetAddressListener {
            override fun onGetAddress(currentAddress: GTAddress) {
                lastAddressPoint = Utils.toLatLng(currentAddress.cityPoint.point!!)
                onCameraMove(lastAddressPoint, !comparePointsWithRounding(lastAddressPoint, lastPoint))
                viewState.setMapPoint(lastAddressPoint, true, showBtnMyLocation(lastAddressPoint))
                viewState.setAddress(currentAddress.cityPoint.name)
            }
        }
    }

    override fun updateView() {
        fillViewFromState()
    }

    private fun fillViewFromState() {
        worker.main.launch {
            selectedField = withContext(worker.bg) { getPreferences().getModel() }.selectedField
            viewState.initUIForSelectedField(selectedField)
            initAddressFieldAndMarker(selectedField)
            if (fillAddressFieldsCheckIsEmpty()) {
                viewState.blockAddressField()
                locationManager.getCurrentLocation(isFromFieldSelected())
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

    private fun showBtnMyLocation(point: LatLng) =
        locationManager.lastCurrentLocation == null || point != locationManager.lastCurrentLocation

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
                        Utils.fromLatLng(lastPoint!!)
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
        viewState.goToSearchAddress(selectedField == FIELD_TO)
    }

    override fun onNextClick() {
        if (orderInteractor.isCanCreateOrder()) {
            viewState.goToCreateOrder()
        } else {
            viewState.navigateBack()
        }
    }

    fun enablePinAnimation() {
        isMarkerAnimating = false
    }

    fun updateLocation() {
        locationManager.getCurrentLocation(isFromFieldSelected())
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

    fun onOkClickResetDialog() {
        sessionInteractor.isAppLanguageChanged = false
        viewState.restartApp()
    }

    /* TODO: убрать андроид-зависимость */
    fun checkPermission(fragment: Fragment) {
        locationManager.checkPermission(fragment)
    }
}
