package com.kg.gettransfer.presentation.presenter

import moxy.MvpPresenter

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.interactor.GeoInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SessionInteractor

import com.kg.gettransfer.presentation.mapper.PointMapper

import com.kg.gettransfer.presentation.view.BaseNewTransferView

import com.kg.gettransfer.sys.domain.GetPreferencesInteractor
import com.kg.gettransfer.sys.domain.SetSelectedFieldInteractor

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.LocationManager

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

    protected val locationManager: LocationManager by inject()

    protected val pointMapper: PointMapper by inject()

    abstract fun updateView()

    /**
     * Fill address fields
     * @return true - from address is empty
     */
    abstract fun fillAddressFieldsCheckIsEmpty(): Boolean

    fun updateCurrentLocation(isFromField: Boolean) {
        locationManager.getCurrentLocation(isFromField)
    }

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
        locationManager.addressListener = null
        locationManager.emptyAddressListener = null
        super.onDestroy()
    }

    companion object {
        const val MIN_HOURLY = 2
        const val DELTA_MAX = 0.000_001
    }
}
