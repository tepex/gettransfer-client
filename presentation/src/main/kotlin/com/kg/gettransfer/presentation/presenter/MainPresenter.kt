package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.interactor.GeoInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.ReviewInteractor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Transfer.Companion.filterRateable

import com.kg.gettransfer.presentation.mapper.PointMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.view.MainView
import com.kg.gettransfer.presentation.view.MainView.Companion.REQUEST_SCREEN
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.MainState
import com.kg.gettransfer.utilities.ScreenNavigationState

import kotlinx.coroutines.delay

import org.koin.core.inject

@InjectViewState
class MainPresenter : BasePresenter<MainView>(), CounterEventListener {
    private val geoInteractor: GeoInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()
    private val reviewInteractor: ReviewInteractor by inject()
    private val nState: MainState by inject()  //to keep info about navigation

    private val pointMapper: PointMapper by inject()
    private val profileMapper: ProfileMapper by inject()

    var screenType = REQUEST_SCREEN

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null
    private var lastCurrentLocation: LatLng? = null

    private var currentLocation: String = ""

    private var markerStateLifted = false
    var isMarkerAnimating = true
    internal var isClickTo: Boolean? = null

    private var idleAndMoveCamera = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        systemInteractor.lastMode = Screens.PASSENGER_MODE
        systemInteractor.selectedField = FIELD_FROM
        geoInteractor.initGeocoder()
        if (accountManager.isLoggedIn) {
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

    override fun attachView(view: MainView) {
        super.attachView(view)
        countEventsInteractor.addCounterListener(this)
        if (accountManager.hasAccount) {
            setCountEvents(countEventsInteractor.eventsCount)
        } else {
            viewState.showBadge(false)
        }
        log.debug("MainPresenter.is user logged in: ${accountManager.isLoggedIn}")
        if (!setAddressFields()) setOwnLocation()
        checkAccount()
        changeUsedField(systemInteractor.selectedField)
        viewState.setTripMode(orderInteractor.hourlyDuration)
    }

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
        with(accountManager) {
            viewState.setProfile(profileMapper.toView(remoteProfile), isLoggedIn, hasAccount)
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
                if (geoInteractor.isGpsEnabled && withGps)
                    fetchDataOnly { geoInteractor.getCurrentLocation() }
                            ?.let {
                                lastCurrentLocation = pointMapper.toLatLng(it)
                                fetchResult { geoInteractor.getAddressByLocation(it) }
                                        .isSuccess()
                                        ?.let { address ->
                                            if (address.cityPoint.point != null) setPointAddress(address)
                                        }
                            }
                else
                    with(fetchResultOnly { geoInteractor.getMyLocationByIp() }) {
                        logIpapiRequest()
                        if (error == null && model.latitude != 0.0 && model.longitude != 0.0)
                            setLocation(model)
                    }
            }
        }
    }

    private suspend fun setLocation(point: Point) {
        //val lngBounds = LatLngBounds.builder().include(LatLng(location.latitude, location.longitude)).build()
        //val latLonPair = getLatLonPair(lngBounds)
        val result = fetchResultOnly { orderInteractor.getAddressByLocation(true, point) }
        if (result.error == null && result.model.cityPoint.point != null) setPointAddress(result.model)
    }

    private fun setPointAddress(currentAddress: GTAddress) {
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
            viewState.blockSelectedField(true, systemInteractor.selectedField)
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
            //val latLonPair: Pair<Point, Point> = getLatLonPair(latLngBounds)

            utils.launchSuspend {
                fetchDataOnly {
                    orderInteractor.getAddressByLocation(
                            systemInteractor.selectedField == FIELD_FROM,
                            pointMapper.fromLatLng(lastPoint!!))
                }
                        ?.let {
                            currentLocation = it.cityPoint.name
                            setAddressInSelectedField(currentLocation)
                        }
                viewState.blockInterface(false)
            }
        } else {
            idleAndMoveCamera = true
            setAddressFields()
        }
    }

    // пригодится, если в запрос добавят bounds
    /*private fun getLatLonPair(latLngBounds: LatLngBounds): Pair<Point, Point> {
        val latLonPair: Pair<Point, Point>
        val nePoint = Point(latLngBounds.northeast.latitude, latLngBounds.northeast.longitude)
        val swPoint = Point(latLngBounds.southwest.latitude, latLngBounds.southwest.longitude)
        latLonPair = Pair(nePoint, swPoint)
        return latLonPair
    }*/

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

    private fun setAddressFields(): Boolean {
        with(orderInteractor) {
            viewState.setAddressTo(to?.address ?: EMPTY_ADDRESS)
            return from.also {
                viewState.setAddressFrom(it?.address ?: EMPTY_ADDRESS)
            } != null
        }
    }

    fun onSearchClick(from: String, to: String, bounds: LatLngBounds, returnBack: Boolean = false) {
        nState.currentState = ScreenNavigationState.NO_STATE
        navigateToFindAddress(
                from,
                to,
                bounds,
                returnBack
        )
    }

    private fun navigateToFindAddress(from: String, to: String, bounds: LatLngBounds, returnBack: Boolean = false) {
        router.navigateTo(Screens.FindAddress(
                from,
                to,
                isClickTo,
                bounds,
                returnBack)
        )
    }

    fun onNextClick(block: (Boolean) -> Unit) {
        if (orderInteractor.isCanCreateOrder()) {
            block(true)
            router.navigateTo(Screens.CreateOrder)
        }
    }

    fun onAboutClick() {
        router.navigateTo(Screens.About(systemInteractor.isOnboardingShowed))
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
        if (accountManager.isLoggedIn) {
            if (accountManager.remoteAccount.isDriver) {
                router.newRootScreen(Screens.Carrier(Screens.CARRIER_MODE))
            } else {
                router.navigateTo(Screens.Carrier(Screens.REG_CARRIER))
            }
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
                if (!isReviewSuggested) checkLastTrip()
                else if (shouldAskRateInMarket)
                    utils.launchSuspend {
                        delay(ONE_SEC_DELAY)
                        viewState.askRateInPlayMarket()
                    }
            }

    private fun checkLastTrip() {
        utils.launchSuspend {
            fetchResultOnly { transferInteractor.getAllTransfers() }
                    .isSuccess()
                    ?.let {
                        getLastTransfer(it.filterRateable())
                                ?.let { lastTransfer ->
                                    checkToShowReview(lastTransfer) }
                    }
        }
    }

    private fun getLastTransfer(transfers: List<Transfer>) =
            transfers.filter { it.status.checkOffers }
                    .sortedByDescending { it.dateToLocal }
                    .firstOrNull()

    private suspend fun checkToShowReview(transfer: Transfer) =
            fetchResultOnly { offerInteractor.getOffers(transfer.id) }
                    .isSuccess()
                    ?.firstOrNull()
                    ?.let { offer ->
                        if (transfer.offersUpdatedAt != null) fetchDataOnly { transferInteractor.setOffersUpdatedDate(transfer.id) }
                        if (offer.isRateAvailable() && !offer.isOfferRatedByUser()) {
                            reviewInteractor.offerIdForReview = offer.id
                            viewState.showRateForLastTrip(
                                    transfer.id,
                                    offer.vehicle.name,
                                    offer.vehicle.color ?: ""
                            )
                            logTransferReviewRequested()
                        }
                    }

    private fun logTransferReviewRequested() =
            analytics.logEvent(Analytics.EVENT_TRANSFER_REVIEW_REQUESTED,
                    createEmptyBundle(),
                    emptyMap())

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
        log.debug("Share action")
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
                if (err.isNotFound()) viewState.setTransferNotFoundError(transferId)
                else viewState.setError(err)
            } else {
                val transfer = transferResult.model
                val transferModel = transfer.map(systemInteractor.transportTypes.map { it.map() })

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

    fun initGoogleApiClient() = geoInteractor.initGoogleApiClient()

    fun disconnectGoogleApiClient() = geoInteractor.disconnectGoogleApiClient()

    companion object {
        const val FIELD_FROM    = "field_from"
        const val FIELD_TO      = "field_to"
        const val EMPTY_ADDRESS = ""

        const val MIN_HOURLY    = 2
        const val ONE_SEC_DELAY = 1000L

        const val MARKER_ELEVATION = 5f
    }
}
