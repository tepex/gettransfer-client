package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.presentation.mapper.PointMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper

import com.kg.gettransfer.presentation.view.MainView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {
    private val routeInteractor: RouteInteractor by inject()
    private val profileMapper: ProfileMapper by inject()

    private val pointMapper: PointMapper by inject()
    private val profileMapper: ProfileMapper by inject()

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null
    private var minDistance: Int = 30

    private var available: Boolean = false
    private var currentLocation: String = ""

    private val MARKER_ELEVATION = 5f
    private var markerStateLifted = false

    var isMarkerAnimating = true
    internal var isClickTo: Boolean? = null

    private var idleAndMoveCamera = true

    @CallSuper
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        systemInteractor.lastMode = Screens.PASSENGER_MODE
        systemInteractor.selectedField = FIELD_FROM
        systemInteractor.initGeocoder()
        if (routeInteractor.from != null) setLastLocation()
        else utils.launchSuspend { updateCurrentLocationAsync().apply { error?.let { Timber.e(it) } } }

        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    @CallSuper
    override fun attachView(view: MainView) {
        super.attachView(view)
        Timber.d("MainPresenter.is user logged in: ${systemInteractor.account.user.loggedIn}")
        viewState.setProfile(profileMapper.toView(systemInteractor.account.user.profile))
        changeUsedField(systemInteractor.selectedField)
        routeInteractor.from?.address?.let { viewState.setAddressFrom(it) }
        viewState.setTripMode(routeInteractor.hourlyDuration)
    }

    @CallSuper
    override fun systemInitialized() {
        super.systemInitialized()
        viewState.setProfile(profileMapper.toView(systemInteractor.account.user.profile))
    }

    fun switchUsedField() {
        when (systemInteractor.selectedField) {
            FIELD_FROM -> changeUsedField(FIELD_TO)
            FIELD_TO   -> changeUsedField(FIELD_FROM)
        }
    }

    fun changeUsedField(field: String) {
        systemInteractor.selectedField = field

        val pointSelectedField: Point? = when (field) {
            FIELD_FROM -> routeInteractor.from?.cityPoint?.point
            FIELD_TO -> routeInteractor.to?.cityPoint?.point
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
            viewState.setMapPoint(latLngPointSelectedField, false)
        }
    }

    fun updateCurrentLocation() = utils.launchSuspend {
        val result = updateCurrentLocationAsync()
        result.error?.let { viewState.setError(it) }
        logEvent(Analytics.MY_PLACE_CLICKED)
    }

    private fun setLastLocation() {
        viewState.blockInterface(true)
        setPointAddress(routeInteractor.from!!)
    }

    private suspend fun updateCurrentLocationAsync(): Result<GTAddress> {
        //viewState.blockInterface(true)
        viewState.blockSelectedField(true, systemInteractor.selectedField)
        val result = utils.asyncAwait { routeInteractor.getCurrentAddress() }
        if (result.error == null) setPointAddress(result.model)
        return result
    }

    private fun setPointAddress(currentAddress: GTAddress) {
        lastAddressPoint = pointMapper.toView(currentAddress.cityPoint.point!!)
        onCameraMove(lastAddressPoint, !comparePointsWithRounding(lastAddressPoint, lastPoint))
        viewState.setMapPoint(lastAddressPoint, true)
        //viewState.setAddressFrom(currentAddress.cityPoint.name!!)
        setAddressInSelectedField(currentAddress.cityPoint.name!!)

        lastAddressPoint = pointMapper.toView(currentAddress.cityPoint.point!!)
    }

    fun onCameraMove(lastPoint: LatLng, animateMarker: Boolean) {
        if (idleAndMoveCamera) {
            if (!markerStateLifted && !isMarkerAnimating && animateMarker) {
                viewState.setMarkerElevation(true, MARKER_ELEVATION)
                markerStateLifted = true
            }
            this.lastPoint = lastPoint
            viewState.moveCenterMarker(lastPoint)
            //viewState.blockInterface(true)
            viewState.blockSelectedField(true, systemInteractor.selectedField)
        }
    }

    fun onCameraIdle(latLngBounds: LatLngBounds) {
        if (idleAndMoveCamera) {
            if (markerStateLifted && !isMarkerAnimating) {
                viewState.setMarkerElevation(false, -MARKER_ELEVATION)
                markerStateLifted = false
            }
            if (lastPoint == null) return
            /* Не запрашивать адрес, если перемещение составило менее minDistance
            val distance = FloatArray(2)
            Location.distanceBetween(lastPoint!!.latitude, lastPoint!!.longitude,
                                    lastAddressPoint.latitude, lastAddressPoint.longitude, distance)
            //if(distance.get(0) < minDistance) return
            */

            lastAddressPoint = lastPoint!!
            val latLonPair: Pair<Point, Point>
            val nePoint = Point(latLngBounds.northeast.latitude, latLngBounds.northeast.longitude)
            val swPoint = Point(latLngBounds.southwest.latitude, latLngBounds.southwest.longitude)
            latLonPair = Pair(nePoint, swPoint)

            utils.launchSuspend {
                val result = utils.asyncAwait {
                    routeInteractor.getAddressByLocation(
                        systemInteractor.selectedField == FIELD_FROM,
                        pointMapper.fromView(lastPoint!!),
                        latLonPair
                    )
                }
                if (result.error != null) {
                    Timber.e("getAddressByLocation", result.error!!)
                    viewState.setError(result.error!!)
                } else {
                    currentLocation = result.model.cityPoint.name!!
                    setAddressInSelectedField(currentLocation)
                }
                viewState.blockInterface(false)
            }
        } else {
            idleAndMoveCamera = true
            setAddressFields()
        }
    }

    private fun setAddressInSelectedField(address: String) {
        when (systemInteractor.selectedField) {
            FIELD_FROM -> viewState.setAddressFrom(address)
            FIELD_TO -> viewState.setAddressTo(address)
        }
    }

    fun enablePinAnimation() { isMarkerAnimating = false }

    fun tripModeSwitched(hourly: Boolean) {
        routeInteractor.apply {
            hourlyDuration = if (hourly) hourlyDuration?: MIN_HOURLY else null
        }
        viewState.changeFields(hourly)
    }

    fun tripDurationSelected(hours: Int) {
        routeInteractor.hourlyDuration = hours
    }

    fun isHourly() = routeInteractor.hourlyDuration != null

    fun setAddressFields() {
        viewState.setAddressFrom(routeInteractor.from?.address ?: "")
        viewState.setAddressTo(routeInteractor.to?.address ?: "")
        viewState.initSearchForm()
    }

    fun onSearchClick(from: String, to: String, bounds: LatLngBounds) { navigateToFindAddress(from, to, bounds) }
    fun onNextClick  (from: String, to: String, bounds: LatLngBounds) { navigateToFindAddress(from, to, bounds) }

    private fun navigateToFindAddress(from: String, to: String, bounds: LatLngBounds) {
        routeInteractor.from?.let { router.navigateTo(Screens.FindAddress(from, to, isClickTo, bounds)) }
    }

    fun onNextClick() {
        if (routeInteractor.from?.cityPoint != null && (routeInteractor.to?.cityPoint != null || routeInteractor.hourlyDuration != null))
            router.navigateTo(Screens.CreateOrder)
    }

    fun onAboutClick() {
        router.navigateTo(Screens.About(false))
        logEvent(Analytics.ABOUT_CLICKED)
    }

    fun readMoreClick() {
        viewState.showReadMoreDialog()
        logEvent(Analytics.BEST_PRICE_CLICKED)
    }

    fun onSettingsClick() {
        router.navigateTo(Screens.Settings)
        logEvent(Analytics.SETTINGS_CLICKED)
    }

    fun onRequestsClick() {
        router.navigateTo(Screens.Requests)
        logEvent(Analytics.TRANSFER_CLICKED)
    }

    fun onLoginClick() {
        login(Screens.PASSENGER_MODE, "")
        logEvent(Analytics.LOGIN_CLICKED)
    }

    fun onBecomeACarrierClick() {
        logEvent(Analytics.DRIVER_CLICKED)
        if (systemInteractor.account.user.loggedIn) {
            if (systemInteractor.account.groups.indexOf(Account.GROUP_CARRIER_DRIVER) >= 0) router.navigateTo(Screens.ChangeMode(Screens.CARRIER_MODE))
            else router.navigateTo(Screens.ChangeMode(Screens.REG_CARRIER))
        }
        else {
            login(Screens.CARRIER_MODE, "")
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

    fun logEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_NAME] = value

        analytics.logEvent(Analytics.EVENT_MENU, createStringBundle(Analytics.PARAM_KEY_NAME, value), map)
    }

    fun onBackClick() {
        if (systemInteractor.selectedField == FIELD_TO) switchUsedField()
        else viewState.onBackClick()
    }

    fun onShareClick() {
        Timber.d("Share action")
        logEvent(Analytics.SHARE)
    }

    companion object {
        @JvmField val FIELD_FROM = "field_from"
        @JvmField val FIELD_TO   = "field_to"

        const val MIN_HOURLY     = 2
    }
}
