package com.kg.gettransfer.presentation.presenter

import moxy.MvpPresenter

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.interactor.GeoInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.mapper.PointMapper
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_FROM
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_TO

import com.kg.gettransfer.presentation.view.BaseNewTransferView

import com.kg.gettransfer.sys.domain.GetPreferencesInteractor
import com.kg.gettransfer.sys.domain.SetSelectedFieldInteractor

import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@Suppress("TooManyFunctions")
abstract class BaseNewTransferPresenter<BV : BaseNewTransferView> : MvpPresenter<BV>(), KoinComponent {
    protected val analytics: Analytics by inject()

    protected val worker: WorkerManager by inject { parametersOf("BaseNewTransferPresenter") }
    protected val getPreferences: GetPreferencesInteractor by inject()
    private val setSelectedField: SetSelectedFieldInteractor by inject()

    protected val geoInteractor: GeoInteractor by inject()
    protected val orderInteractor: OrderInteractor by inject()
    protected val sessionInteractor: SessionInteractor by inject()

    protected val pointMapper: PointMapper by inject()

    protected var lastCurrentLocation: LatLng? = null

    abstract fun updateView()

    /**
     * Fill address fields
     * @return true - from address is empty
     */
    abstract fun fillAddressFieldsCheckIsEmpty(): Boolean

    fun updateCurrentLocation(isFromField: Boolean) {
        updateCurrentLocationAsync(isFromField)
    }

    open fun updateCurrentLocationAsync(isFromField: Boolean) {
        viewState.defineAddressRetrieving { withGps ->
            worker.main.launch {
                withContext(worker.bg) {
                    if (geoInteractor.isGpsEnabled && withGps) {
                        getLocationFromGPS(isFromField)
                    } else {
                        getLocationFromIpApi(isFromField)
                    }
                }
            }
        }
    }

    private suspend fun getLocationFromGPS(isFromField: Boolean) {
        val locationResult = geoInteractor.getCurrentLocation()
        val currentLocation = locationResult.isSuccess()
        if (currentLocation != null && !locationResult.isGeoError()) {
            lastCurrentLocation = pointMapper.toLatLng(currentLocation)
            val address = geoInteractor.getAddressByLocation(currentLocation).isSuccess()
            if (address?.cityPoint?.point != null) {
                withContext(worker.main.coroutineContext) { setPointAddress(address) }
            } else {
                getLocationFromIpApi(isFromField)
            }
        } else {
            getLocationFromIpApi(isFromField)
        }
    }

    private suspend fun getLocationFromIpApi(isFromField: Boolean) {
        val result = geoInteractor.getMyLocationByIp()
        logAddressByIpRequest()
        if (result.error == null && result.model.latitude != 0.0 && result.model.longitude != 0.0) {
            withContext(worker.main.coroutineContext) { setLocation(isFromField, result.model) }
        } else {
            withContext(worker.main.coroutineContext) { setEmptyAddress() }
        }
    }

    private suspend fun setLocation(isFromField: Boolean, point: Point) = worker.main.launch {
        val result = withContext(worker.bg) {
            orderInteractor.getAddressByLocation(isFromField, point)
        }
        if (result.error == null && result.model.cityPoint.point != null) {
            setPointAddress(result.model)
        } else {
            setEmptyAddress()
        }
    }

    open suspend fun setPointAddress(currentAddress: GTAddress) {
        when (withContext(worker.bg) { getPreferences().getModel() }.selectedField) {
            FIELD_FROM -> orderInteractor.from = currentAddress
            FIELD_TO   -> orderInteractor.to   = currentAddress
        }
    }

    abstract fun setEmptyAddress()

    private fun showBtnMyLocation(point: LatLng) = lastCurrentLocation == null || point != lastCurrentLocation

    fun navigateToFindAddress(isClickTo: Boolean = false) {
        viewState.goToSearchAddress(isClickTo)
    }

    abstract fun onNextClick()

    private fun comparePointsWithRounding(point1: LatLng?, point2: LatLng?): Boolean {
        if (point2 == null || point1 == null) return false

        var latDiff = point1.latitude - point1.latitude
        if (latDiff < 0) latDiff *= -1
        var lngDiff = point2.longitude - point2.longitude
        if (lngDiff < 0) lngDiff *= -1
        return latDiff < DELTA_MAX && lngDiff < DELTA_MAX
    }

    private fun logAddressByIpRequest() = analytics.logSingleEvent(Analytics.EVENT_IPAPI_REQUEST)

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }

    companion object {
        const val MIN_HOURLY = 2
        const val DELTA_MAX = 0.000_001
    }
}
