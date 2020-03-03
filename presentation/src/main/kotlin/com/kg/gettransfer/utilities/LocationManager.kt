package com.kg.gettransfer.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender

import androidx.fragment.app.Fragment

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.core.domain.GTAddress
import com.kg.gettransfer.core.domain.Point
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.domain.interactor.GeoInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.extensions.map
import com.kg.gettransfer.presentation.presenter.SearchPresenter

import com.kg.gettransfer.sys.domain.GetPreferencesInteractor

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

import pub.devrel.easypermissions.EasyPermissions

import timber.log.Timber

/**
 * Get current location through GPS or IP address
 */
class LocationManager(val context: Context) : KoinComponent {

    private val worker: WorkerManager by inject { parametersOf(LocationManager::class.simpleName) }
    private val analytics: Analytics by inject()
    private val getPreferences: GetPreferencesInteractor by inject()

    private val geoInteractor: GeoInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()

    internal var addressListener: OnGetAddressListener? = null
    internal var emptyAddressListener: OnGetEmptyAddressListener? = null

    var lastCurrentLocation: LatLng? = null

    fun getCurrentLocation(isFromField: Boolean?, useOnlyGps: Boolean = false) {
        worker.main.launch {
            withContext(worker.bg) {
                if (geoInteractor.isGpsEnabled && hasPermission()) {
                    getLocationFromGPS(isFromField, useOnlyGps)
                } else {
                    if (!useOnlyGps) getLocationFromIpApi(isFromField)
                }
            }
        }
    }

    private suspend fun getLocationFromGPS(isFromField: Boolean?, useOnlyGps: Boolean) {
        val locationResult = geoInteractor.getCurrentLocation()
        val currentLocation = locationResult.isSuccess()
        if (currentLocation != null && !locationResult.isGeoError()) {
            lastCurrentLocation = currentLocation.map()
            val address = geoInteractor.getAddressByLocation(currentLocation).isSuccess()
            if (address?.cityPoint?.point != null) {
                withContext(worker.main.coroutineContext) { setPointAddress(isFromField, address) }
            } else {
                if (!useOnlyGps) getLocationFromIpApi(isFromField)
            }
        } else {
            if (!useOnlyGps) getLocationFromIpApi(isFromField)
        }
    }

    private suspend fun setPointAddress(isFromField: Boolean?, currentAddress: GTAddress) {
        isFromField?.let {
            when (withContext(worker.bg) { getPreferences().getModel() }.selectedField) {
                SearchPresenter.FIELD_FROM -> orderInteractor.from = currentAddress
                SearchPresenter.FIELD_TO -> orderInteractor.to = currentAddress
            }
        }
        addressListener?.onGetAddress(currentAddress)
    }

    private suspend fun getLocationFromIpApi(isFromField: Boolean?) {
        val result = geoInteractor.getMyLocationByIp()
        logAddressByIpRequest()
        if (result.error == null && result.model.latitude != 0.0 && result.model.longitude != 0.0) {
            withContext(worker.main.coroutineContext) { setLocation(isFromField, result.model) }
        } else {
            withContext(worker.main.coroutineContext) { setEmptyAddress() }
        }
    }

    private suspend fun setLocation(isFromField: Boolean?, point: Point) = worker.main.launch {
        val result = withContext(worker.bg) {
            orderInteractor.getAddressByLocation(isFromField, point)
        }
        if (result.error == null) {
            result.model.cityPoint.point?.let {
                lastCurrentLocation = it.map()
                setPointAddress(isFromField, result.model)
            }
        } else {
            setEmptyAddress()
        }
    }

    private fun setEmptyAddress() {
        emptyAddressListener?.onGetEmptyAddress()
    }

    private fun logAddressByIpRequest() = analytics.logSingleEvent(Analytics.EVENT_IPAPI_REQUEST)

    private fun hasPermission() = EasyPermissions.hasPermissions(context, *PERMISSIONS)

    fun removeAddressListeners() {
        addressListener?.let { addressListener == null }
        emptyAddressListener?.let { emptyAddressListener == null }
    }

    fun checkPermission(activity: Activity): Boolean {
        val hasPermission = EasyPermissions.hasPermissions(activity, *PERMISSIONS)
        if (!hasPermission) {
            EasyPermissions.requestPermissions(
                activity,
                context.getString(R.string.LNG_LOCATION_ACCESS),
                RC_LOCATION,
                *PERMISSIONS
            )
        }
        return hasPermission
    }

    fun checkPermission(fragment: Fragment): Boolean {
        val hasPermission = EasyPermissions.hasPermissions(fragment.requireContext(), *PERMISSIONS)
        if (!hasPermission) {
            EasyPermissions.requestPermissions(
                fragment,
                context.getString(R.string.LNG_LOCATION_ACCESS),
                RC_LOCATION,
                *PERMISSIONS
            )
        }
        return hasPermission
    }

    fun checkDeviceSettingLocation(activity: Activity, resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(context)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(activity, REQUEST_TURN_DEVICE_LOCATION_ON)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d("Error getting location settings resolution: $sendEx.message")
                }
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getCurrentLocation(null, true)
            }
        }
    }

    companion object {
        val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        const val RC_LOCATION = 2222
        const val REQUEST_TURN_DEVICE_LOCATION_ON = 22
    }

    /**
     * Interface definition for a callback to be invoked when a location is received
     */
    interface OnGetAddressListener {
        fun onGetAddress(currentAddress: GTAddress)
    }

    /**
     * Interface definition for a callback to be invoked when a location is empty
     */
    interface OnGetEmptyAddressListener {
        fun onGetEmptyAddress()
    }
}
