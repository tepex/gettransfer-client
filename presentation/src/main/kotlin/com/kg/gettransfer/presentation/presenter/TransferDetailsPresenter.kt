package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.os.Handler
import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.eventListeners.SystemEventListener
import com.kg.gettransfer.presentation.delegate.DriverCoordinate
import com.kg.gettransfer.domain.eventListeners.CoordinateEventListener
import com.kg.gettransfer.domain.interactor.CoordinateInteractor

import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.prefs.PreferencesImpl
import com.kg.gettransfer.presentation.ui.icons.CarIconResourceProvider
import com.kg.gettransfer.presentation.delegate.CoordinateRequester
import com.kg.gettransfer.presentation.mapper.*

import com.kg.gettransfer.presentation.model.*

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.presentation.view.TransferDetailsView

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber
import java.util.Calendar

@InjectViewState
class TransferDetailsPresenter : BasePresenter<TransferDetailsView>(), CoordinateEventListener, SystemEventListener {
    private val routeInteractor: RouteInteractor by inject()
    private val reviewInteractor: ReviewInteractor by inject()
    private val coordinateInteractor: CoordinateInteractor by inject()

    private val profileMapper: ProfileMapper by inject()
    private val routeMapper: RouteMapper by inject()
    private val transferMapper: TransferMapper by inject()
    private val reviewRateMapper: ReviewRateMapper by inject()
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

    @CallSuper
    override fun attachView(view: TransferDetailsView) {
        super.attachView(view)
        systemInteractor.addListener(this)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            result.error?.let { checkResultError(it) }
            if (result.error != null && !result.fromCache) viewState.setError(result.error!!)
            else {
                val transfer = result.model
                transfer.from.point?.let { startCoordinate = pointMapper.toLatLng(it) }
                transfer.from.let { fromPoint = cityPointMapper.toView(it) }
                transfer.to?.let { toPoint = cityPointMapper.toView(it) }
                hourlyDuration = transfer.duration

                transferModel = transferMapper.toView(transfer)
                var offer: Offer? = null

                if (transferModel.status.checkOffers) {
                    val offersResult = utils.asyncAwait { offerInteractor.getOffers(transfer.id) }
                    if ((offersResult.error == null || (offersResult.error != null && offersResult.fromCache)) && offersResult.model.size == 1){
                        offer = offersResult.model.first()
                        offerModel = offerMapper.toView(offer)
                        reviewInteractor.offerIdForReview = offer.id
                        if (allowOfferInfo(transferModel)) viewState.setOffer(offerModel, transferModel.countChilds)
                    }
                }

                val showRate = offer?.isRated()?.not()?:false
                viewState.setTransfer(transferModel, profileMapper.toView(systemInteractor.account.user.profile), showRate)

                if (transfer.to != null) {
                    val r = utils.asyncAwait { routeInteractor.getRouteInfo(transfer.from.point!!, transfer.to!!.point!!, true, false, systemInteractor.currency.currencyCode) }
                    r.cacheError?.let { viewState.setError(it) }
                    setRouteTransfer(transfer, r.model)
                } else if (transfer.duration != null) setHourlyTransfer(transfer)

            }
            viewState.blockInterface(false)

        }
    }

    @CallSuper
    override fun detachView(view: TransferDetailsView?) {
        super.detachView(view)
        driverCoordinate = null  // assign null to avoid drawing marker in detached screen
        isCameraUpdatedForCoordinates = false
        systemInteractor.removeListener(this)
    }

    private fun allowOfferInfo(transfer: TransferModel): Boolean {
        if(transfer.status != Transfer.Status.NEW &&
                transfer.status != Transfer.Status.CANCELED &&
                transfer.status != Transfer.Status.OUTDATED) {
            val waitDetailsDate = (transfer.dateTimeReturn ?: transfer.dateTime).let {
                val calendar = Calendar.getInstance()
                calendar.time = it
                calendar.apply {
                    add(Calendar.MINUTE, transfer.time ?: Utils.convertHoursToMinutes(transfer.duration!!))
                    add(Calendar.MINUTE, Utils.convertHoursToMinutes(24))
                }
                calendar.time
            }
            if (transfer.status == Transfer.Status.PERFORMED ||
                    waitDetailsDate.after(Calendar.getInstance().time)) return true
        }
        return false
    }

    fun onCenterRouteClick() { track?.let { viewState.centerRoute(it) } }

    fun onCancelRequestClicked() { viewState.showAlertCancelRequest() }

    fun onRepeatTransferClicked() {
        fromPoint?.let { routeInteractor.from = GTAddress(cityPointMapper.fromView(it), null, null, null, null) }
        toPoint?.let { routeInteractor.to = GTAddress(cityPointMapper.fromView(it), null, null, null, null) }
        hourlyDuration?.let { routeInteractor.hourlyDuration = it }

        if (routeInteractor.isCanCreateOrder()) router.navigateTo(Screens.CreateOrder)
    }

    fun onChatClick(){
        router.navigateTo(Screens.Chat(transferId))
    }

    private fun setRouteTransfer(transfer: Transfer, route: RouteInfo) {
        routeModel = routeMapper.getView(
            route.distance,
            route.polyLines,
            transfer.from.name!!,
            transfer.to!!.name!!,
            transfer.from.point!!,
            transfer.to!!.point!!,
            SystemUtils.formatDateTime(transferModel.dateTime)
        )
        routeModel?.let {
            polyline = Utils.getPolyline(it)
            track = polyline!!.track
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
            val result = utils.asyncAwait { transferInteractor.cancelTransfer(transferId, "") }
            if (result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
            } else viewState.recreateActivity()
            viewState.blockInterface(false)
        }
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

    fun rateTrip(rating: Float) {
        if (rating.toInt() == ReviewInteractor.MAX_RATE) {
            with(reviewInteractor) {
                utils.launchSuspend { sendTopRate() }
                logAverageRate(ReviewInteractor.MAX_RATE.toDouble())
                if (systemInteractor.appEntersForMarketRate != PreferencesImpl.IMMUTABLE) {
                    viewState.askRateInPlayMarket()
                    logReviewRequest()
                }
                else viewState.thanksForRate()
            }
        } else viewState.showDetailRate(rating)
    }

    fun sendReview(list: List<ReviewRateModel>, feedBackComment: String) = utils.launchSuspend {
        val result = utils.asyncAwait {
            reviewInteractor.sendRates(list.map { reviewRateMapper.fromView(it) }, feedBackComment)
        }
        logAverageRate(list.map { it.rateValue }.average())
        logDetailRate(list, feedBackComment)
        if (result.error != null) { /* some error for analytics */ }
        viewState.thanksForRate()
    }

    fun onRateInStore() {
        systemInteractor.appEntersForMarketRate = ReviewInteractor.APP_RATED_IN_MARKET
        viewState.showRateInPlayMarket()
    }

    fun onReviewCanceled() {
        viewState.closeRateWindow()
        reviewInteractor.rateCanceled()
    }

    fun logEventGetOffer(key: String, value: String) {
        val map = mutableMapOf<String, Any?>()
        map[key] = value
        analytics.logEvent(Analytics.EVENT_GET_OFFER, createStringBundle(key, value), map)
    }

    private fun logAverageRate(rate: Double) =
        analytics.logEvent(
            Analytics.REVIEW_AVERAGE,
            createStringBundle(Analytics.REVIEW,rate.toString()),
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
            viewState.moveCarMarker(bearing, coordinates, show) }
        coordinateInteractor.coordinateEventListener = this
    }

    /*
    When detached - no need to update, so call coordinate delegate safely
     */
    override fun onLocationReceived(coordinate: Coordinate) {
        driverCoordinate?.property = coordinate
        with(coordinate) {
            if(!isCameraUpdatedForCoordinates) {
                viewState.updateCamera(mutableListOf(LatLng(lat, lon)).also { list -> startCoordinate?.let { list.add(it) } })
                isCameraUpdatedForCoordinates = true
            }
        }
    }

    fun getMarkerIcon(offerModel: OfferModel) = CarIconResourceProvider.getVehicleIcon(offerModel.vehicle)

    override fun onSocketConnected() {
        coordinateRequester.request()
    }

    override fun onSocketDisconnected() {}

    companion object {
        const val FIELD_EMAIL = "field_email"
        const val FIELD_PHONE = "field_phone"
        const val OPERATION_COPY = "operation_copy"
        const val OPERATION_OPEN = "operation_open"
    }
}
