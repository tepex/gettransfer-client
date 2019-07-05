package com.kg.gettransfer.presentation.presenter

import android.os.Handler

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener
import com.kg.gettransfer.domain.eventListeners.SocketEventListener

import com.kg.gettransfer.domain.interactor.CoordinateInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.ReviewInteractor

import com.kg.gettransfer.domain.model.Coordinate
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.domain.model.ReviewRate.RateType.DRIVER
import com.kg.gettransfer.domain.model.ReviewRate.RateType.PUNCTUALITY
import com.kg.gettransfer.domain.model.ReviewRate.RateType.VEHICLE

import com.kg.gettransfer.extensions.finishChainAndBackTo

import com.kg.gettransfer.prefs.PreferencesImpl

import com.kg.gettransfer.presentation.delegate.CoordinateRequester
import com.kg.gettransfer.presentation.delegate.DriverCoordinate

import com.kg.gettransfer.presentation.mapper.CityPointMapper
import com.kg.gettransfer.presentation.mapper.PointMapper
import com.kg.gettransfer.presentation.mapper.RouteMapper

import com.kg.gettransfer.presentation.model.*

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.icons.transport.CarIconResourceProvider

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.TransferDetailsView

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.inject

@InjectViewState
class TransferDetailsPresenter : BasePresenter<TransferDetailsView>(), CoordinateEventListener, SocketEventListener {
    private val orderInteractor: OrderInteractor by inject()
    private val reviewInteractor: ReviewInteractor by inject()
    private val coordinateInteractor: CoordinateInteractor by inject()

    private val routeMapper: RouteMapper by inject()
    private val cityPointMapper: CityPointMapper by inject()
    private val pointMapper: PointMapper by inject()

    private lateinit var transferModel: TransferModel
    private lateinit var offerModel: OfferModel
    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null

    private var fromPoint: CityPointModel? = null
    private var toPoint: CityPointModel? = null
    private var hourlyDuration: Int? = null

    internal var transferId = 0L
    private var driverCoordinate: DriverCoordinate? = null
    private var isCameraUpdatedForCoordinates = false
    private var startCoordinate: LatLng? = null
    private var offer: Offer? = null

    override fun attachView(view: TransferDetailsView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            fetchData { transferInteractor.getTransfer(transferId) }
                    ?.let { transfer ->
                        setTransferFields(transfer)
                        setOffer(transfer.id)
                                ?.let {
                                    if (transferModel.status.checkOffers)
                                        offer = it
                                }
                        viewState.setTransfer(transferModel)

                        updateRatingState()
                        setTransferType(transfer)
                    }
            viewState.blockInterface(false)
        }
        socketInteractor.addSocketListener(this)
    }

    private fun setTransferFields(transfer: Transfer) {
        transfer.from.point?.let { startCoordinate = pointMapper.toLatLng(it) }
        transfer.from.let { fromPoint = cityPointMapper.toView(it) }
        transfer.to?.let { toPoint = cityPointMapper.toView(it) }
        hourlyDuration = transfer.duration

        transferModel = transferMapper.toView(transfer)
    }

    private suspend fun setOffer(transferId: Long) =
        fetchData { offerInteractor.getOffers(transferId) }
            ?.let {
                if (it.size == 1) {
                    val offer = it.first()
                    offerModel = offerMapper.toView(offer)
                    reviewInteractor.offerIdForReview = offer.id
                    if (transferModel.showOfferInfo) viewState.setOffer(offerModel, transferModel.countChilds)
                    offer
                } else null
            }

    private suspend fun setTransferType(transfer: Transfer) {
        if (transfer.to != null) {
            fetchResult {
                orderInteractor.getRouteInfo(
                    RouteInfoRequest(
                        transfer.from.point!!,
                        transfer.to!!.point!!,
                        false,
                        false,
                        sessionInteractor.currency.code,
                        null
                    )
                )
            }.also {
                it.cacheError?.let { e -> viewState.setError(e) }
                setRouteTransfer(transfer, it.model)
            }
        } else if (transfer.duration != null) setHourlyTransfer(transfer)
    }

    override fun detachView(view: TransferDetailsView?) {
        super.detachView(view)
        driverCoordinate = null  // assign null to avoid drawing marker in detached screen
        isCameraUpdatedForCoordinates = false
        socketInteractor.removeSocketListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        reviewInteractor.releaseReviewData()
    }

    fun onCenterRouteClick() {
        track?.let { viewState.centerRoute(it) }
    }

    fun onCancelRequestClicked() {
        viewState.showAlertCancelRequest()
    }

    fun onRepeatTransferClicked() {
        fromPoint?.let {
            orderInteractor.from = GTAddress(
                cityPointMapper.fromView(it),
                emptyList<String>(),
                transferModel.from,
                GTAddress.parseAddress(transferModel.from)
            )
        }

        toPoint?.let { to ->
            orderInteractor.to = GTAddress(
                cityPointMapper.fromView(to),
                emptyList<String>(),
                transferModel.to,
                transferModel.to?.let { GTAddress.parseAddress(it) }
            )
        }
        if(toPoint == null) orderInteractor.to = null

        orderInteractor.hourlyDuration = hourlyDuration

        if (orderInteractor.isCanCreateOrder()) router.navigateTo(Screens.CreateOrder)
    }

    fun onChatClick() {
        router.navigateTo(Screens.Chat(transferId))
    }

    private fun setRouteTransfer(transfer: Transfer, route: RouteInfo) {
        routeModel = routeMapper.getView(
            route.distance,
            route.polyLines,
            transfer.from.name,
            transfer.to!!.name,
            transfer.from.point!!,
            transfer.to!!.point!!,
            SystemUtils.formatDateTime(transferModel.dateTime)
        )
        routeModel?.let {
            polyline = Utils.getPolyline(it)
            track = polyline!!.track
            if (polyline!!.isVerticalRoute) viewState.setMapBottomPadding()
            viewState.setRoute(polyline!!, it)
        }
    }

    private fun setHourlyTransfer(transfer: Transfer) {
        val from = transfer.from.point!!
        val point = LatLng(from.latitude, from.longitude)
        track = Utils.getCameraUpdateForPin(point)
        viewState.setPinHourlyTransfer(
            transferModel.from,
            SystemUtils.formatDateTime(transferModel.dateTime),
            point,
            track!!
        )
    }

    fun cancelRequest(isCancel: Boolean) {
        if (!isCancel) return
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = fetchResultOnly { transferInteractor.cancelTransfer(transferId, "") }
            if (result.isError()) {
                result.error?.let { viewState.setError(it) }
            } else {
                showMainActivity()
            }
            viewState.blockInterface(false)
        }
    }

    private fun showMainActivity() {
        viewState.showCancelRequestToast()
        router.finishChainAndBackTo(Screens.MainPassenger())
    }

    fun makeFieldOperation(field: String, operation: String, text: String) {
        when (operation) {
            OPERATION_COPY -> viewState.copyText(text)
            OPERATION_OPEN -> {
                when (field) {
                    FIELD_PHONE -> callPhone(text)
                    FIELD_EMAIL -> sendEmail(text, transferId)
                }
            }
        }
    }

    fun rateTrip(rating: Float, isNeedCheckStoreRate: Boolean) {
        if (isNeedCheckStoreRate) {
            if (rating.toInt() == ReviewInteractor.MAX_RATE) {
                with(reviewInteractor) {
                    utils.launchSuspend { sendTopRate() }
                    logAverageRate(ReviewInteractor.MAX_RATE.toFloat())
                    if (systemInteractor.appEntersForMarketRate != PreferencesImpl.IMMUTABLE) {
                        viewState.askRateInPlayMarket()
                        logReviewRequest()
                    }
                }
                val doubleRating = rating.toDouble()
                offer = offer?.copy(
                    ratings = offer?.ratings?.copy(
                        vehicle = doubleRating,
                        driver = doubleRating,
                        fair = doubleRating
                    )
                )
                updateRatingState()
            } else {
                offer?.let {
                    viewState.showDetailRate(
                        RatingsModel(RatingsModel.NO_RATING, rating, rating, rating),
                        it.id,
                        it.passengerFeedback.orEmpty()
                    )
                }
            }
        } else
            offer?.let {
                viewState.showDetailRate(
                    it.ratings?.map() ?: RatingsModel.EMPTY,
                    it.id,
                    it.passengerFeedback.orEmpty()
                )
            }
    }

    private fun logAverageRate(rate: Float) =
        analytics.logEvent(
            Analytics.REVIEW_AVERAGE,
            createStringBundle(Analytics.REVIEW, rate.toString()),
            mapOf(Analytics.REVIEW to rate)
        )

    private fun logReviewRequest() =
        analytics.logEvent(
            Analytics.EVENT_APP_REVIEW_REQUESTED,
            createEmptyBundle(),
            mapOf()
        )

    fun logTransferReviewRequested() =
        analytics.logEvent(
            Analytics.EVENT_TRANSFER_REVIEW_REQUESTED,
            createEmptyBundle(),
            mapOf()
        )

    private val coordinateRequester = object : CoordinateRequester {
        override fun request() = coordinateInteractor.initCoordinatesReceiving(transferId)
    }

    fun initCoordinates() {
        driverCoordinate = DriverCoordinate(Handler(), coordinateRequester) { bearing, coordinates, show ->
            viewState.moveCarMarker(bearing, coordinates, show)
        }
        coordinateInteractor.coordinateEventListener = this
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

    fun ratingChanged(list: List<ReviewRateModel>, userFeedback: String) {
        offer = offer?.copy(
            ratings = offer?.ratings?.copy(
                vehicle = list.firstOrNull { it.rateType == VEHICLE }?.rateValue?.toDouble() ?: 0.0,
                driver = list.firstOrNull { it.rateType == DRIVER }?.rateValue?.toDouble() ?: 0.0,
                fair = list.firstOrNull { it.rateType == PUNCTUALITY }?.rateValue?.toDouble() ?: 0.0
            ),
            passengerFeedback = userFeedback
        )
        updateRatingState()
    }

    fun ratingChangeCancelled() {
        updateRatingState()
    }

    fun clickComment(comment: String) {
        viewState.showCommentEditor(comment)
    }

    fun commentChanged(comment: String) {
        offer?.let {
            setComment(comment)
            viewState.showYourDataProgress(true)
            utils.launchSuspend {
                fetchResult { reviewInteractor.pushComment() }
                    .also {
                        if (it.error == null) {
                            offer = offer?.copy(passengerFeedback = comment)
                            updateRatingState()
                        }
                        viewState.showYourDataProgress(false)
                    }
            }
        }
    }

    private fun setComment(comment: String) {
        reviewInteractor.comment = comment
    }

    private fun updateRatingState() {
        val available = offer?.isRateAvailable() ?: false
        val isRated = offer?.isOfferRatedByUser() ?: false
        if (available && !isRated) reviewInteractor.offerIdForReview = offer?.id ?: 0
        viewState.showCommonRating(available && !isRated)
        viewState.showYourRateMark(isRated, offer?.ratings?.average ?: 0.0)
        viewState.showYourComment(isRated, offer?.passengerFeedback.orEmpty())
    }

    override fun onSocketConnected() {
        coordinateRequester.request()
    }

    override fun onSocketDisconnected() {}

    fun onDownloadVoucherClick() = downloadManager.downloadVoucher(transferId)

    companion object {
        const val FIELD_EMAIL = "field_email"
        const val FIELD_PHONE = "field_phone"
        const val OPERATION_COPY = "operation_copy"
        const val OPERATION_OPEN = "operation_open"
    }
}
