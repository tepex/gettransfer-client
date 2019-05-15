package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.eventListeners.CounterEventListener

import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.presentation.mapper.PointMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.mapper.ReviewRateMapper
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.model.ReviewRateModel

import com.kg.gettransfer.presentation.view.MainView
import com.kg.gettransfer.presentation.view.MainView.Companion.REQUEST_SCREEN
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.MainState
import com.kg.gettransfer.utilities.ScreenNavigationState
import kotlinx.coroutines.delay

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class MainPresenter : BasePresenter<MainView>(), CounterEventListener {
    private val orderInteractor: OrderInteractor by inject()
    private val reviewInteractor: ReviewInteractor by inject()
    private val nState: MainState by inject()  //to keep info about navigation

    private val pointMapper: PointMapper by inject()
    private val profileMapper: ProfileMapper by inject()
    private val reviewRateMapper: ReviewRateMapper by inject()

    var screenType = REQUEST_SCREEN

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null
    private var lastCurrentLocation: LatLng? = null
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
        if (systemInteractor.account.user.loggedIn) {
            registerPushToken()
            checkReview()
            utils.launchSuspend {
                utils.asyncAwait { transferInteractor.getAllTransfers() }
                setCountEvents(countEventsInteractor.eventsCount)
            }
        }

        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    @CallSuper
    override fun attachView(view: MainView) {
        super.attachView(view)
        countEventsInteractor.addCounterListener(this)
        if (systemInteractor.account.user.loggedIn) {
            setCountEvents(countEventsInteractor.eventsCount)
        } else {
            viewState.showBadge(false)
        }
        Timber.d("MainPresenter.is user logged in: ${systemInteractor.account.user.loggedIn}")
        if (!setAddressFields()) setOwnLocation()
        checkAccount()
        changeUsedField(systemInteractor.selectedField)
        viewState.setTripMode(orderInteractor.hourlyDuration)
    }

    @CallSuper
    override fun detachView(view: MainView?) {
        super.detachView(view)
        countEventsInteractor.removeCounterListener(this)
    }

    fun setScreenState(hasRequestView: Boolean) {
        when {
            nState.currentState == MainState.CHOOSE_POINT_ON_MAP ->
                viewState.openMapToSetPoint()

            screenType == REQUEST_SCREEN && !hasRequestView      ->
                viewState.recreateRequestFragment()
        }
    }

    private fun checkAccount() {
        with(systemInteractor.account.user) {
            viewState.setProfile(hasAccount, profile.email, profile.fullName)
        }
    }

    fun resetState() {
        nState.currentState = ScreenNavigationState.NO_STATE
    }

    private fun setOwnLocation() {
        if (orderInteractor.from != null) setLastLocation() else updateCurrentLocation()
    }

    private fun setCountEvents(count: Int) {
        viewState.showBadge(count > 0)
        if (count > 0) viewState.setCountEvents(count)
    }


    override fun updateCounter() {
        utils.launchSuspend { setCountEvents(countEventsInteractor.eventsCount) }
    }

    override fun onNewOffer(offer: Offer): OfferModel {
        utils.launchSuspend { setCountEvents(countEventsInteractor.eventsCount) }
        return super.onNewOffer(offer)
    }

    @CallSuper
    override fun systemInitialized() {
        super.systemInitialized()
        checkAccount()
    }

    fun switchUsedField() {
        when (systemInteractor.selectedField) {
            FIELD_FROM -> changeUsedField(FIELD_TO)
            FIELD_TO -> changeUsedField(FIELD_FROM)
        }
    }

    private fun changeUsedField(field: String) {
        systemInteractor.selectedField = field

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

    fun updateCurrentLocation() {
        updateCurrentLocationAsync()
        logButtons(Analytics.MY_PLACE_CLICKED)
    }

    private fun setLastLocation() {
        viewState.blockInterface(true)
        orderInteractor.from?.let {
            setPointAddress(it)
        }
    }

    private fun updateCurrentLocationAsync() {
        //viewState.blockInterface(true)
        viewState.blockSelectedField(true, systemInteractor.selectedField)
        viewState.defineAddressRetrieving { withGps ->
            utils.launchSuspend {
                if (systemInteractor.isGpsEnabled && withGps)
                    fetchDataOnly { orderInteractor.getCurrentAddress() }
                            ?.let {
                                lastCurrentLocation = it.cityPoint.point?.let { point -> pointMapper.toLatLng(point) }
                                setPointAddress(it)
                            }
                else
                    with(fetchResultOnly { systemInteractor.getMyLocation() }) {
                        logIpapiRequest()
                        if (error == null && model.latitude != null && model.longitude != null)
                            setLocation(model)
                    }
            }
        }
    }

    private suspend fun setLocation(location: Location) {
        val point = Point(location.latitude!!, location.longitude!!)
        val lngBounds = LatLngBounds.builder().include(LatLng(location.latitude!!, location.longitude!!)).build()
        val latLonPair = getLatLonPair(lngBounds)
        val result = fetchResultOnly { orderInteractor.getAddressByLocation(true, point, latLonPair) }
        if (result.error == null && result.model.cityPoint.point != null) setPointAddress(result.model)
    }

    private fun setPointAddress(currentAddress: GTAddress) {
        lastAddressPoint = pointMapper.toLatLng(currentAddress.cityPoint.point!!)
        onCameraMove(lastAddressPoint, !comparePointsWithRounding(lastAddressPoint, lastPoint))
        viewState.setMapPoint(lastAddressPoint, true, showBtnMyLocation(lastAddressPoint))
        //viewState.setAddressFrom(currentAddress.cityPoint.name!!)
        setAddressInSelectedField(currentAddress.cityPoint.name!!)

        lastAddressPoint = pointMapper.toLatLng(currentAddress.cityPoint.point!!)
    }

    private fun showBtnMyLocation(point: LatLng) = lastCurrentLocation == null || point != lastCurrentLocation

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
            val latLonPair: Pair<Point, Point> = getLatLonPair(latLngBounds)

            utils.launchSuspend {
                fetchData {
                    orderInteractor.getAddressByLocation(
                            systemInteractor.selectedField == FIELD_FROM,
                            pointMapper.fromLatLng(lastPoint!!),
                            latLonPair)
                }
                        ?.let {
                            currentLocation = it.cityPoint.name!!
                            setAddressInSelectedField(currentLocation)
                        }
                viewState.blockInterface(false)
            }
        } else {
            idleAndMoveCamera = true
            setAddressFields()
        }
    }

    private fun getLatLonPair(latLngBounds: LatLngBounds): Pair<Point, Point> {
        val latLonPair: Pair<Point, Point>
        val nePoint = Point(latLngBounds.northeast.latitude, latLngBounds.northeast.longitude)
        val swPoint = Point(latLngBounds.southwest.latitude, latLngBounds.southwest.longitude)
        latLonPair = Pair(nePoint, swPoint)
        return latLonPair
    }

    private fun setAddressInSelectedField(address: String) {
        when (systemInteractor.selectedField) {
            FIELD_FROM -> viewState.setAddressFrom(address)
            FIELD_TO -> viewState.setAddressTo(address)
        }
    }


    fun enablePinAnimation() {
        isMarkerAnimating = false
    }

    fun tripModeSwitched(hourly: Boolean) {
        orderInteractor.apply {
            hourlyDuration = if (hourly) hourlyDuration ?: MIN_HOURLY else null
        }
        if (systemInteractor.selectedField == FIELD_TO) changeUsedField(FIELD_FROM)
        viewState.changeFields(hourly)
    }

    fun tripDurationSelected(hours: Int) {
        orderInteractor.hourlyDuration = hours
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

    fun onSearchClick(from: String, to: String, bounds: LatLngBounds, returnBack: Boolean = false) {
        nState.currentState = ScreenNavigationState.NO_STATE
        navigateToFindAddress(from, to, bounds, returnBack)
    }

    private fun navigateToFindAddress(from: String, to: String, bounds: LatLngBounds, returnBack: Boolean = false) {
        orderInteractor.from?.let { router.navigateTo(Screens.FindAddress(from, to, isClickTo, bounds, returnBack)) }
    }

    fun onNextClick(block: (Boolean) -> Unit) {
        if (orderInteractor.isCanCreateOrder()) {
            block(true)
            router.navigateTo(Screens.CreateOrder)
        }
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
        } else {
            login(Screens.CARRIER_MODE, "")
        }
    }

    fun onSupportClick() {
        router.navigateTo(Screens.Support)
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

    private fun checkReview() =
            with(reviewInteractor) {
                if (!isReviewSuggested) viewState.showRateForLastTrip()
                else if (shouldAskRateInMarket)
                    utils.launchSuspend {
                        delay(ONE_SEC_DELAY)
                        viewState.askRateInPlayMarket()
                    }
            }

    private fun logEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.PARAM_KEY_NAME] = value
        analytics.logEvent(Analytics.EVENT_MENU, createStringBundle(Analytics.PARAM_KEY_NAME, value), map)
    }

    private fun logButtons(event: String) {
        analytics.logEventToFirebase(event, null)
        analytics.logEventToYandex(event, null)
    }

    fun onBackClick() {
        if (systemInteractor.selectedField == FIELD_TO) switchUsedField()
        else viewState.onBackClick(nState.currentState == MainState.CHOOSE_POINT_ON_MAP,
                isClickTo ?: true)
    }

    fun onShareClick() {
        Timber.d("Share action")
        logEvent(Analytics.SHARE)
        router.navigateTo(Screens.Share())
    }

    private fun logIpapiRequest() =
            analytics.logEvent(
                    Analytics.EVENT_IPAPI_REQUEST,
                    createEmptyBundle(),
                    mapOf()
            )

    fun rateTransfer(transferId: Long, rate: Int) {
        utils.launchSuspend {
            val transferResult = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if (transferResult.error != null) {
                val err = transferResult.error!!
                if (err.isNotFound()) {
                    viewState.setError(ApiException(ApiException.NOT_FOUND, "Transfer $transferId not found!"))
                } else viewState.setError(err)
            } else {
                val transfer = transferResult.model
                val transferModel = transferMapper.toView(transfer)

                if (transferModel.status.checkOffers) {
                    val offersResult = utils.asyncAwait { offerInteractor.getOffers(transfer.id) }
                    if (offersResult.error == null && offersResult.model.size == 1) {
                        val offer = offersResult.model.first()
                        reviewInteractor.offerIdForReview = offer.id
                        if (rate == ReviewInteractor.MAX_RATE) {
                            reviewInteractor.apply {
                                sendTopRate()
                                viewState.thanksForRate()
                            }
                        } else {
                            viewState.showDetailedReview(rate.toFloat(), offer.id)
                        }
                    }
                }
            }
        }
    }

    fun onStartScreenOrderNote() {
        systemInteractor.startScreenOrder = true
    }

    companion object {
        const val FIELD_FROM    = "field_from"
        const val FIELD_TO      = "field_to"
        const val EMPTY_ADDRESS = ""

        const val MIN_HOURLY    = 2
        const val ONE_SEC_DELAY = 1000L
    }
}
