package com.kg.gettransfer.presentation.presenter

import android.app.Activity
import android.os.Handler

import moxy.InjectViewState

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.core.domain.CityPoint
import com.kg.gettransfer.core.domain.GTAddress
import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener

import com.kg.gettransfer.domain.interactor.CoordinateInteractor
import com.kg.gettransfer.domain.interactor.ReviewInteractor

import com.kg.gettransfer.domain.model.Coordinate
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.domain.model.ReviewRate.RateType.DRIVER
import com.kg.gettransfer.domain.model.ReviewRate.RateType.COMMUNICATION
import com.kg.gettransfer.domain.model.ReviewRate.RateType.VEHICLE

import com.kg.gettransfer.extensions.map

import com.kg.gettransfer.presentation.delegate.DriverCoordinate

import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.icons.transport.CarIconResourceProvider

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.TransferDetailsView

import com.kg.gettransfer.sys.domain.GetPreferencesInteractor
import com.kg.gettransfer.sys.domain.Preferences
import com.kg.gettransfer.sys.domain.SetAppEntersInteractor

import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.CommunicationManager
import com.kg.gettransfer.utilities.LocationManager

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
@Suppress("TooManyFunctions")
class TransferDetailsPresenter : BasePresenter<TransferDetailsView>(), CoordinateEventListener {
    private val coordinateInteractor: CoordinateInteractor by inject()
    private val getPreferences: GetPreferencesInteractor by inject()
    private val worker: WorkerManager by inject { parametersOf("TransferDetailsPresenter") }
    private val setAppEnters: SetAppEntersInteractor by inject()
    private val communicationManager: CommunicationManager by inject()
    private val locationManager: LocationManager by inject()

    private lateinit var transferModel: TransferModel
    private lateinit var offerModel: OfferModel
    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null

    private var fromPoint: CityPoint? = null
    private var toPoint: CityPoint? = null
    private var hourlyDuration: Int? = null

    internal var transferId = 0L
    private var driverCoordinate: DriverCoordinate? = null
    private var isCameraUpdatedForCoordinates = false
    private var startCoordinate: LatLng? = null
    private var offer: Offer? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchData { transferInteractor.getTransfer(transferId) }?.let { transfer ->
                setTransferFields(transfer)
                setTransferType(transfer)
                getOffer()
            }
            viewState.blockInterface(false)
        }
        initAddressListener()
    }

    private fun initAddressListener() {
        locationManager.addressListener = object : LocationManager.OnGetAddressListener {
            override fun onGetAddress(currentAddress: GTAddress) {
                viewState.moveToLocationMarker(locationManager.lastCurrentLocation)
            }
        }
    }

    private suspend fun getOffer() {
        setOffer(transferId)?.let { found ->
            if (transferModel.status.offerMatched) {
                offer = found
                updateRatingState()
            }
        } ?: run { viewState.setBookNowOfferInfo(transferModel.isBookNow()) }
    }

    private suspend fun setOffer(transferId: Long) =
        fetchData { offerInteractor.getOffers(transferId) }?.let { list ->
            if (list.size == 1) {
                val offer = list.first()
                offerModel = offer.map()
                reviewInteractor.offerRateID = offer.id
                viewState.setOffer(offerModel, transferModel.countChilds, transferModel.unreadMessagesCount)
                viewState.setBookNowOfferInfo(false)
                offer
            } else {
                null
            }
        }

    private suspend fun setTransferFields(transfer: Transfer) {
        transfer.from.point?.let { startCoordinate = it.map() }
        fromPoint = transfer.from
        toPoint = transfer.to
        hourlyDuration = transfer.duration

        transferModel = transfer.map(configsManager.getConfigs().transportTypes.map { it.map() })
        viewState.setTransfer(transferModel)
    }

    private suspend fun setTransferType(transfer: Transfer) {
        transfer.to?.let { to ->
            transfer.from.point?.let { fromPoint ->
                to.point?.let { toPoint ->
                    fetchResult {
                        orderInteractor.getRouteInfo(
                            RouteInfoRequest(
                                from = fromPoint,
                                to = toPoint,
                                hourlyDuration = transfer.duration,
                                withPrices = false,
                                returnWay = false,
                                currency = sessionInteractor.currency.code,
                                dateTo = null,
                                dateReturn = null
                            )
                        )
                    }.also { result ->
                        result.cacheError?.let { viewState.setError(it) }
                        setRouteTransfer(transfer, result.model)
                    }
                }
            }
        } ?: transfer.duration?.let { setHourlyTransfer(transfer) }
    }

    override fun detachView(view: TransferDetailsView?) {
        super.detachView(view)
        driverCoordinate?.requestCoordinates = false
        driverCoordinate = null  // assign null to avoid drawing marker in detached screen
        isCameraUpdatedForCoordinates = false
    }

    override fun onDestroy() {
        worker.cancel()
        reviewInteractor.releaseReviewData()
        coordinateInteractor.removeCoordinateListener(this)
        locationManager.removeAddressListeners()
        super.onDestroy()
    }

    fun onCenterRouteClick() {
        track?.let { viewState.centerRoute(it) }
    }

    fun onCancelRequestClicked() {
        viewState.showCancelationReasonsList()
    }

    fun onRepeatTransferClicked() {
        fromPoint?.let { fromPoint ->
            orderInteractor.from = GTAddress(
                fromPoint,
                emptyList<String>(),
                transferModel.from,
                GTAddress.parseAddress(transferModel.from)
            )
        }

        toPoint?.let { to ->
            orderInteractor.to = GTAddress(
                to,
                emptyList<String>(),
                transferModel.to,
                transferModel.to?.let { GTAddress.parseAddress(it) }
            )
        }

        if (toPoint == null) {
            orderInteractor.to = null
        }
        orderInteractor.hourlyDuration = hourlyDuration
        orderInteractor.selectedTransports = transferModel.transportTypes.map { it.id }.toSet()

        if (orderInteractor.isCanCreateOrder()) {
            router.navigateTo(Screens.CreateOrder)
        }
    }

    fun onChatClick() {
        router.navigateTo(Screens.Chat(transferId))
    }

    @Suppress("UnsafeCallOnNullableType")
    private fun setRouteTransfer(transfer: Transfer, route: RouteInfo) {
        routeModel = RouteModel(
            transfer.from.name,
            transfer.to!!.name,
            transfer.from.point!!,
            transfer.to!!.point!!,
            SystemUtils.formatDateTime(transferModel.dateTime),
            transfer.distance,
            transfer.dateReturnLocal != null,
            route.polyLines
        )
        routeModel?.let { routeModel ->
            polyline = Utils.getPolyline(routeModel)
            polyline?.let { polyline ->
                track = polyline.track
                if (polyline.isVerticalRoute) {
                    viewState.setMapBottomPadding()
                }
                viewState.setRoute(polyline, routeModel)
            }
        }
    }

    private fun setHourlyTransfer(transfer: Transfer) {
        transfer.from.point?.let { fromPoint ->
            val point = LatLng(fromPoint.latitude, fromPoint.longitude)
            Utils.getCameraUpdateForPin(point)?.let { cameraUpdate ->
                track = cameraUpdate
                viewState.setPinHourlyTransfer(
                    transferModel.from,
                    SystemUtils.formatDateTime(transferModel.dateTime),
                    point,
                    cameraUpdate
                )
            }
        }
    }

    fun cancelRequest(reason: String) {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResultOnly { transferInteractor.cancelTransfer(transferId, reason) }.also { result ->
                result.error?.let { viewState.setError(it) }
                result.isSuccess()?.let { transfer ->
                    setTransferFields(transfer)
                    viewState.showAlertRestoreRequest()
                }
                viewState.blockInterface(false)
            }
        }
    }

    fun restoreRequest() {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchResultOnly { transferInteractor.restoreTransfer(transferId) }.also { result ->
                result.error?.let { viewState.setError(it) }
                result.isSuccess()?.let { transfer ->
                    setTransferFields(transfer)
                }
                viewState.blockInterface(false)
            }
        }
    }

    fun makeFieldOperation(field: String, operation: String, text: String) {
        when (operation) {
            OPERATION_COPY -> viewState.copyField(text)
            OPERATION_OPEN -> when (field) {
                FIELD_PHONE -> callPhone(text)
                FIELD_EMAIL -> sendEmail(transferId, text)
            }
        }
    }

    fun callPhone(phone: String) = communicationManager.callPhone(phone)

    private fun sendEmail(transferId: Long?, emailCarrier: String?) =
        communicationManager.sendEmail(transferId, emailCarrier)

    fun rateTrip(rating: Float, isNeedCheckStoreRate: Boolean) {
        offer?.let { reviewInteractor.setOfferReview(it) }
        if (isNeedCheckStoreRate) {
            reviewInteractor.setRates(rating)
            if (rating.toInt() == ReviewInteractor.MAX_RATE) {
                worker.main.launch {
                    withContext(worker.bg) {
                        reviewInteractor.sendRates()
                        viewState.showRateAnimation()
                    }
                    analytics.logEvent(
                        Analytics.EVENT_REVIEW_AVERAGE,
                        Analytics.REVIEW,
                        ReviewInteractor.MAX_RATE.toFloat()
                    )
                    val appEnters = withContext(worker.bg) { getPreferences().getModel() }.appEnters
                    if (appEnters != Preferences.IMMUTABLE) {
                        viewState.askRateInPlayMarket()
                        analytics.logSingleEvent(Analytics.EVENT_APP_REVIEW_REQUESTED)
                    }
                }
                return
            }
        }
        viewState.showDetailRate()
    }

    fun logTransferReviewRequested() = analytics.logSingleEvent(Analytics.EVENT_TRANSFER_REVIEW_REQUESTED)

    fun initCoordinates() {
        if (driverCoordinate == null) driverCoordinate = DriverCoordinate(Handler()) { bearing, coordinates, show ->
            viewState.moveCarMarker(bearing, coordinates, show)
        }
        driverCoordinate?.let { it.transfersIds = listOf(transferId) }
        coordinateInteractor.addCoordinateListener(this)
    }

    /*
    When detached - no need to update, so call coordinate delegate safely
     */
    override fun onLocationReceived(coordinate: Coordinate) {
        driverCoordinate?.property = coordinate
        with(coordinate) {
            if (!isCameraUpdatedForCoordinates) {
                viewState.updateCamera(
                    mutableListOf(
                        LatLng(
                            lat,
                            lon
                        )
                    ).also { list -> startCoordinate?.let { list.add(it) } })
                isCameraUpdatedForCoordinates = true
            }
        }
    }

    fun getMarkerIcon(offerModel: OfferModel) = CarIconResourceProvider.getVehicleIcon(offerModel.vehicle)

    fun ratingChanged(list: List<ReviewRate>, userFeedback: String) {
        offer = offer?.copy(
            ratings = offer?.ratings?.copy(
                vehicle = list.firstOrNull { it.rateType == VEHICLE }?.rateValue?.toDouble(),
                driver = list.firstOrNull { it.rateType == DRIVER }?.rateValue?.toDouble(),
                communication = list.firstOrNull { it.rateType == COMMUNICATION }?.rateValue?.toDouble()
            ),
            passengerFeedback = userFeedback
        )
        updateRatingState()
    }

    fun ratingChangeCancelled() {
        updateRatingState()
    }

    private fun updateRatingState() {
        val available = offer?.isRateAvailable() ?: false
        val neededRate = offer?.isNeededRateOffer() ?: false
        if (available && neededRate) reviewInteractor.offerRateID = offer?.id ?: 0
        viewState.showCommonRating(available && neededRate)
        viewState.showYourRateMark(!neededRate, offer?.ratings?.average ?: 0.0)
    }

    fun onDownloadVoucherClick() = downloadManager.downloadVoucher(transferId)

    fun redirectToPlayMarket() = worker.main.launch {
        withContext(worker.bg) { setAppEnters(ReviewInteractor.APP_RATED_IN_MARKET) }
        viewState.goToGooglePlay()
    }

    fun onSupportClick(transferId: Long) {
        viewState.showSupportScreen(transferId)
    }

    fun getCurrentLocation(activity: Activity, resolve: Boolean = true) {
        locationManager.checkDeviceSettingLocation(activity, resolve)
    }

    fun checkLocationPermission(activity: Activity) = locationManager.checkPermission(activity)

    companion object {
        const val FIELD_EMAIL = "field_email"
        const val FIELD_PHONE = "field_phone"
        const val OPERATION_COPY = "operation_copy"
        const val OPERATION_OPEN = "operation_open"
    }
}
