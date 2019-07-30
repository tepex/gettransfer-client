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
import com.kg.gettransfer.utilities.MainState
import kotlinx.coroutines.Job
import org.koin.core.KoinComponent
import org.koin.core.get

import org.koin.core.inject
import ru.terrakok.cicerone.Router

open class BaseNewTransferPresenter<BV : BaseNewTransferView> : MvpPresenter<BV>(), KoinComponent {
    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    protected val router: Router by inject()
    protected val analytics: Analytics by inject()

    protected val geoInteractor: GeoInteractor by inject()
    protected val orderInteractor: OrderInteractor by inject()
    protected val systemInteractor: SystemInteractor by inject()
    protected val nState: MainState by inject()  //to keep info about navigation

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

    fun fillViewFromState() {
        if (!orderInteractor.isAddressesValid())
            changeUsedField(NewTransferMainPresenter.FIELD_FROM)
        else
            changeUsedField(systemInteractor.selectedField)

        if (fillAddressFieldsCheckIsEmpty()) updateCurrentLocation()

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
            viewState.setAddressTo(to?.address ?: NewTransferMainPresenter.EMPTY_ADDRESS)
            return from.also {
                viewState.setAddressFrom(it?.address ?: NewTransferMainPresenter.EMPTY_ADDRESS)
            } == null
        }
    }

    fun updateCurrentLocation() {
        updateCurrentLocationAsync()
        analytics.logSingleEvent(Analytics.MY_PLACE_CLICKED)
    }

    private fun setLastLocation() {
        blockSelectedField(FIELD_FROM)
        orderInteractor.from?.let {
            setPointAddress(it)
        }
    }

    private fun updateCurrentLocationAsync() {
        blockSelectedField(systemInteractor.selectedField)
        viewState.defineAddressRetrieving { withGps ->
            utils.launchSuspend {
                if (geoInteractor.isGpsEnabled && withGps) {
                    utils.asyncAwait { geoInteractor.getCurrentLocation() }
                            .isSuccess()
                            ?.let {
                                lastCurrentLocation = pointMapper.toLatLng(it)

                                utils.asyncAwait { geoInteractor.getAddressByLocation(it) }
                                        .isSuccess()
                                        ?.let { address ->
                                            if (address.cityPoint.point != null) setPointAddress(address)
                                        }
                            }
                } else {
                    utils.asyncAwait { geoInteractor.getMyLocationByIp() }.let {
                        logIpapiRequest()
                        if (it.error == null && it.model.latitude != 0.0 && it.model.longitude != 0.0)
                            setLocation(it.model)
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
        orderInteractor.apply {
            hourlyDuration = if (hourly) hourlyDuration ?: MIN_HOURLY else null
            viewState.setHourlyDuration(hourlyDuration)
        }
        viewState.updateTripView(hourly)
        if (systemInteractor.selectedField == FIELD_TO) changeUsedField(FIELD_FROM)
    }

    fun tripDurationSelected(hours: Int) {
        orderInteractor.apply {
            hourlyDuration = hours
            viewState.setHourlyDuration(hourlyDuration)
        }
    }

    fun isHourly() = orderInteractor.hourlyDuration != null

    fun setAddressFields(): Boolean {
        with(orderInteractor) {
            viewState.setAddressTo(to?.address ?: EMPTY_ADDRESS)
            return from.also {
                viewState.setAddressFrom(it?.address ?: EMPTY_ADDRESS)
            } != null
        }
    }

    fun navigateToFindAddress(from: String, to: String, isClickTo: Boolean = false, returnBack: Boolean = false) {
        router.navigateTo(Screens.FindAddress(
                from,
                to,
                isClickTo,
                returnBack)
        )
    }

    fun onNextClick(block: (Boolean) -> Unit) {
        if (orderInteractor.isCanCreateOrder()) {
            block(true)
            router.navigateTo(Screens.CreateOrder)
        }
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
    }
}
