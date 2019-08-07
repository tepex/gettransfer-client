package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.MvpPresenter

import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.GeoInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.mapper.PointMapper
import com.kg.gettransfer.presentation.view.BaseNewTransferView

import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.NewTransferState

import kotlinx.coroutines.Job

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

import ru.terrakok.cicerone.Router

@Suppress("TooManyFunctions")
open class BaseNewTransferPresenter<BV : BaseNewTransferView> : MvpPresenter<BV>(), KoinComponent {
    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    protected val router: Router by inject()
    protected val analytics: Analytics by inject()

    protected val geoInteractor: GeoInteractor by inject()
    protected val orderInteractor: OrderInteractor by inject()
    protected val systemInteractor: SystemInteractor by inject()
    protected val nState: NewTransferState by inject()  // to keep info about navigation

    protected val pointMapper: PointMapper by inject()

    protected var lastCurrentLocation: LatLng? = null

    protected var isVisible: Boolean = true

    override fun attachView(view: BV) {
        super.attachView(view)
        updateView(isVisible)
    }

    open fun updateView(isVisibleView: Boolean) {
        this.isVisible = isVisibleView
    }

    open fun fillViewFromState() {
        if (!orderInteractor.isAddressesValid()) {
            changeUsedField(NewTransferMainPresenter.FIELD_FROM)
        } else {
            changeUsedField(systemInteractor.selectedField)
        }

        if (fillAddressFieldsCheckIsEmpty()) {
            updateCurrentLocation()
        }

        viewState.setHourlyDuration(orderInteractor.hourlyDuration)
        viewState.updateTripView(isHourly())
    }

    fun switchUsedField() {
        when (systemInteractor.selectedField) {
            FIELD_FROM -> changeUsedField(FIELD_TO)
            FIELD_TO -> changeUsedField(FIELD_FROM)
        }
    }

    open fun changeUsedField(field: String) {
        systemInteractor.selectedField = field
    }

    /**
     * Fill address fields
     * @return true - from address is empty
     */
    fun fillAddressFieldsCheckIsEmpty(): Boolean {
        with(orderInteractor) {
            viewState.setAddressTo(to?.address ?: EMPTY_ADDRESS)
            return from.also {
                viewState.setAddressFrom(it?.address ?: EMPTY_ADDRESS)
            } == null
        }
    }

    fun updateCurrentLocation() {
        updateCurrentLocationAsync()
        analytics.logSingleEvent(Analytics.MY_PLACE_CLICKED)
    }

    private fun updateCurrentLocationAsync() {
        blockSelectedField(systemInteractor.selectedField)
        viewState.defineAddressRetrieving { withGps ->
            utils.launchSuspend {
                if (geoInteractor.isGpsEnabled && withGps) {
                    utils.asyncAwait { geoInteractor.getCurrentLocation() }.isSuccess()?.let { loc ->
                        lastCurrentLocation = pointMapper.toLatLng(loc)

                        utils.asyncAwait { geoInteractor.getAddressByLocation(loc) }.isSuccess()?.let { address ->
                            if (address.cityPoint.point != null) {
                                setPointAddress(address)
                            }
                        }
                    }
                } else {
                    val result = utils.asyncAwait { geoInteractor.getMyLocationByIp() }
                    logIpapiRequest()
                    if (result.error == null && result.model.latitude != 0.0 && result.model.longitude != 0.0) {
                        setLocation(result.model)
                    }
                }
            }
        }
    }

    private suspend fun setLocation(point: Point) {
        val result = utils.asyncAwait { orderInteractor.getAddressByLocation(true, point) }
        if (result.error == null && result.model.cityPoint.point != null) setPointAddress(result.model)
    }

    open fun setPointAddress(currentAddress: GTAddress) {
        when (systemInteractor.selectedField) {
            FIELD_FROM -> orderInteractor.from = currentAddress
            FIELD_TO -> orderInteractor.to = currentAddress
        }
    }

    private fun showBtnMyLocation(point: LatLng) = lastCurrentLocation == null || point != lastCurrentLocation

    fun blockSelectedField(field: String) {
        when (field) {
            FIELD_FROM -> viewState.blockFromField()
            FIELD_TO -> viewState.blockToField()
        }
    }

    fun setAddressInSelectedField(address: String) {
        when (systemInteractor.selectedField) {
            FIELD_FROM -> viewState.setAddressFrom(address)
            FIELD_TO -> viewState.setAddressTo(address)
        }
    }

    fun tripModeSwitched(hourly: Boolean) {
        updateDuration(if (hourly) orderInteractor.hourlyDuration ?: MIN_HOURLY else null)
        viewState.updateTripView(hourly)
        if (systemInteractor.selectedField == FIELD_TO) changeUsedField(FIELD_FROM)
    }

    fun updateDuration(hours: Int?) {
        orderInteractor.apply {
            hourlyDuration = hours
            viewState.setHourlyDuration(hourlyDuration)
        }
    }

    fun isHourly() = orderInteractor.hourlyDuration != null

    fun navigateToFindAddress(from: String, to: String, isClickTo: Boolean = false, returnToMain: Boolean = false) {
        router.navigateTo(Screens.FindAddress(from, to, isClickTo, returnToMain))
    }

    fun onNextClick(block: (Boolean) -> Unit) {
        if (orderInteractor.isCanCreateOrder()) {
            block(true)
            router.navigateTo(Screens.CreateOrder)
        }
    }

    private fun comparePointsWithRounding(point1: LatLng?, point2: LatLng?): Boolean {
        if (point2 == null || point1 == null) return false

        var latDiff = point1.latitude - point1.latitude
        if (latDiff < 0) latDiff *= -1
        var lngDiff = point2.longitude - point2.longitude
        if (lngDiff < 0) lngDiff *= -1
        return latDiff < DELTA_MAX && lngDiff < DELTA_MAX
    }

    private fun logIpapiRequest() = analytics.logSingleEvent(Analytics.EVENT_IPAPI_REQUEST)

    fun showHourlyDurationDialog() {
        viewState.showHourlyDurationDialog(orderInteractor.hourlyDuration)
    }

    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    companion object {
        const val FIELD_FROM = "field_from"
        const val FIELD_TO = "field_to"
        const val EMPTY_ADDRESS = ""

        const val MIN_HOURLY = 2
        const val DELTA_MAX = 0.000_001
    }
}
