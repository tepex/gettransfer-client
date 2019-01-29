package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.os.Handler
import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.model.Transfer.Companion.filterCompleted
import com.kg.gettransfer.prefs.PreferencesImpl

import com.kg.gettransfer.presentation.mapper.PointMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.mapper.ReviewRateMapper
import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.mapper.TransferMapper
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.model.ReviewRateModel
import com.kg.gettransfer.presentation.model.RouteModel

import com.kg.gettransfer.presentation.ui.SystemUtils

import com.kg.gettransfer.presentation.view.MainView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.service.OfferServiceConnection

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {
    private val routeInteractor: RouteInteractor by inject()
    private val reviewInteractor: ReviewInteractor by inject()
    private val offerServiceConnection: OfferServiceConnection by inject()

    private val pointMapper: PointMapper by inject()
    private val profileMapper: ProfileMapper by inject()
    private val transferMapper: TransferMapper by inject()
    private val routeMapper: RouteMapper by inject()
    private val reviewRateMapper: ReviewRateMapper by inject()

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
        if (systemInteractor.account.user.loggedIn) { registerPushToken(); checkReview() }
        checkNewMessagesCached()

        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    @CallSuper
    override fun attachView(view: MainView) {
        super.attachView(view)
        Timber.d("MainPresenter.is user logged in: ${systemInteractor.account.user.loggedIn}")
        if (routeInteractor.from != null) setLastLocation() else updateCurrentLocation()
        viewState.setProfile(profileMapper.toView(systemInteractor.account.user.profile))
        changeUsedField(systemInteractor.selectedField)
        routeInteractor.from?.address?.let { viewState.setAddressFrom(it) }
        viewState.setTripMode(routeInteractor.hourlyDuration)
        setCountEvents(systemInteractor.eventsCount)
    }

    private fun setCountEvents(count: Int) {
        viewState.showBadge(count > 0)
        if (count > 0) viewState.setCountEvents(count)
    }

    override fun onNewOffer(offer: Offer): OfferModel {
        utils.launchSuspend{ setCountEvents(systemInteractor.eventsCount) }
        return super.onNewOffer(offer)
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

    private fun changeUsedField(field: String) {
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
        updateCurrentLocationAsync()
        logButtons(Analytics.MY_PLACE_CLICKED)
    }

    private fun setLastLocation() {
        viewState.blockInterface(true)
        setPointAddress(routeInteractor.from!!)
    }

    private suspend fun updateCurrentLocationAsync(): Result<GTAddress> {
        //viewState.blockInterface(true)
        viewState.blockSelectedField(true, systemInteractor.selectedField)
        utils.asyncAwait { routeInteractor.getCurrentAddress() }.also {
            if (it.error != null) {
                viewState.setError(it.error!!)
                val locationResult = utils.asyncAwait { systemInteractor.getMyLocation() }
                if (locationResult.error == null
                        && locationResult.model.latitude != null
                        && locationResult.model.longitude != null) setLocation(locationResult.model)
            }
            else setPointAddress(it.model)
            return it
        }

    }

    private suspend fun setLocation(location: Location) {
        val point = Point(location.latitude!!, location.longitude!!)
        val lngBounds = LatLngBounds.builder().include(LatLng(location.latitude!!, location.longitude!!)).build()
        val latLonPair = getLatLonPair(lngBounds)
        val result = utils.asyncAwait { routeInteractor.getAddressByLocation(true, point, latLonPair) }
        if (result.error == null && result.model.cityPoint.point != null) setPointAddress(result.model)
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
            val latLonPair: Pair<Point, Point> = getLatLonPair(latLngBounds)

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

    fun enablePinAnimation() { isMarkerAnimating = false }

    fun tripModeSwitched(hourly: Boolean) {
        routeInteractor.apply {
            hourlyDuration = if (hourly) hourlyDuration?: MIN_HOURLY else null
        }
        if(systemInteractor.selectedField == FIELD_TO) changeUsedField(FIELD_FROM)
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

    private fun showRateForLastTrip() {     //get all completed transfers -> get last transfer -> get offer -> showRate view
        utils.launchSuspend {
            val result = utils.asyncAwait { transferInteractor.getAllTransfers() }

            if (result.error != null) viewState.setError(result.error!!)
            else result.isNotError()?.let {
                getLastTransfer(it.filterCompleted())
                        ?.let { transfer -> checkToShowReview(transfer) }
            }
        }
    }

    private fun checkReview() =
        with(reviewInteractor) {
            if (!isReviewSuggested) showRateForLastTrip()
            else if (shouldAskRateInMarket) Handler().postDelayed({ viewState.askRateInPlayMarket() }, ONE_SEC_DELAY)
        }

    private fun getLastTransfer(transfers: List<Transfer>) =
        transfers.filter { it.status.checkOffers }
            .sortedByDescending { it.dateToLocal }
            .firstOrNull()

    private suspend fun checkToShowReview(transfer: Transfer) =
        utils.asyncAwait { offerInteractor.getOffers(transfer.id) }
            .isNotError()
            ?.firstOrNull()
            ?.let { offer ->
                if (!offer.isRated()) {
                    val routeModel = if(transfer.to != null) createRouteModel(transfer) else null
                    reviewInteractor.offerIdForReview = offer.id
                    viewState.openReviewForLastTrip(
                        transferMapper.toView(transfer),
                        LatLng(transfer.from.point!!.latitude, transfer.from.point!!.longitude),
                        offer.vehicle.name,
                        offer.vehicle.color?:"",
                        routeModel
                    )
                    logTransferReviewRequested()
                }
            }

    fun onReviewCanceled() {
        reviewInteractor.rateCanceled()
        viewState.cancelReview()
    }

    fun onRateClicked(rate: Float) {
        if (rate.toInt() == ReviewInteractor.MAX_RATE) {
            logAverageRate(ReviewInteractor.MAX_RATE.toDouble())
            reviewInteractor.apply {
                utils.launchSuspend { sendTopRate() }
                if (systemInteractor.appEntersForMarketRate != PreferencesImpl.IMMUTABLE) {
                    viewState.askRateInPlayMarket()
                    logAppReviewRequest()
                }
                else viewState.thanksForRate()
            }
        } else viewState.showDetailedReview(rate)
    }

    fun sendReview(list: List<ReviewRateModel>, comment: String) = utils.launchSuspend {
        val result = utils.asyncAwait {
            reviewInteractor.sendRates(list.map { reviewRateMapper.fromView(it) }, comment)
        }
        logAverageRate(list.map { it.rateValue }.average())
        logDetailRate(list, comment)
        if (result.error != null) { /* some error for analytics */ }
        viewState.thanksForRate()
    }

    fun onRateInStore() {
        systemInteractor.appEntersForMarketRate = ReviewInteractor.APP_RATED_IN_MARKET
        viewState.showRateInPlayMarket()
    }

    fun onTransferDetailsClick(transferId: Long) = router.navigateTo(Screens.Details(transferId))

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
        if (systemInteractor.selectedField == FIELD_TO) switchUsedField() else viewState.onBackClick()
    }

    fun onShareClick() {
        Timber.d("Share action")
        logEvent(Analytics.SHARE)
        router.navigateTo(Screens.Share())
    }

    private suspend fun createRouteModel(transfer: Transfer): RouteModel {
        val route = routeInteractor.getRouteInfo(transfer.from.point!!, transfer.to!!.point!!, false, false, systemInteractor.currency.currencyCode).model
        return routeMapper.getView(
            route.distance,
            route.polyLines,
            transfer.from.name!!,
            transfer.to!!.name!!,
            transfer.from.point!!,
            transfer.to!!.point!!,
            SystemUtils.formatDateTime(transferMapper.toView(transfer).dateTime)
        )
    }

    private fun logAverageRate(rate: Double) =
        analytics.logEvent(
            Analytics.REVIEW_AVERAGE,
            createStringBundle(Analytics.REVIEW, rate.toString()),
            mapOf(Analytics.REVIEW to rate)
        )

    private fun logDetailRate(list: List<ReviewRateModel>, comment: String) {
        val map = mutableMapOf<String, String?>()
        val bundle = Bundle()
        list.forEach {
            val key = analytics.reviewDetailKey(it.rateType.type)
            bundle.putInt(key, it.rateValue)
            map[key] = it.rateValue.toString()
        }
        map[Analytics.REVIEW_COMMENT] = comment
        bundle.putString(Analytics.REVIEW_COMMENT, comment)
        analytics.logEvent(Analytics.EVENT_TRANSFER_REVIEW_DETAILED, bundle, map)
    }

    private fun logTransferReviewRequested() =
            analytics.logEvent(Analytics.EVENT_TRANSFER_REVIEW_REQUESTED,
                    createEmptyBundle(),
                    emptyMap())

    private fun logAppReviewRequest() =
        analytics.logEvent(
            Analytics.EVENT_APP_REVIEW_REQUESTED,
            createEmptyBundle(),
            emptyMap()
        )

    companion object {
        const val FIELD_FROM = "field_from"
        const val FIELD_TO   = "field_to"

        const val MIN_HOURLY          = 2
        const val ONE_SEC_DELAY       = 1000L
    }
}
