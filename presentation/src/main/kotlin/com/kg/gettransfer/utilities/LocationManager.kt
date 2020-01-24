package com.kg.gettransfer.utilities

import android.Manifest
import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.interactor.GeoInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.presentation.mapper.PointMapper
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import pub.devrel.easypermissions.EasyPermissions

class LocationManager(val context: Context): KoinComponent {

    private val worker: WorkerManager by inject { parametersOf(LocationManager::class.simpleName) }
    private val analytics: Analytics by inject()
    private val getPreferences: GetPreferencesInteractor by inject()

    private val pointMapper: PointMapper by inject()
    private val geoInteractor: GeoInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()

    var lastCurrentLocation: LatLng? = null

    fun getCurrentLocation(isFromField: Boolean) {
        worker.main.launch {
            withContext(worker.bg) {
                if (geoInteractor.isGpsEnabled && isGpsEnabled()) {
                    getLocationFromGPS(isFromField)
                } else {
                    getLocationFromIpApi(isFromField)
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

    private suspend fun setPointAddress(currentAddress: GTAddress) {
        when (withContext(worker.bg) { getPreferences().getModel() }.selectedField) {
            SearchPresenter.FIELD_FROM -> orderInteractor.from = currentAddress
            SearchPresenter.FIELD_TO -> orderInteractor.to   = currentAddress
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

    private fun setEmptyAddress() {

    }

    private fun logAddressByIpRequest() = analytics.logSingleEvent(Analytics.EVENT_IPAPI_REQUEST)

    private fun isGpsEnabled() = EasyPermissions.hasPermissions(context, *PERMISSIONS)

    companion object {
        val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}