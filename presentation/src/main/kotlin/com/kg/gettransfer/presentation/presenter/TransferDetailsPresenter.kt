package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.interactor.ReviewInteractor

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.OfferMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper
import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.mapper.TransferMapper

import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.ui.Utils

import com.kg.gettransfer.presentation.view.TransferDetailsView

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class TransferDetailsPresenter : BasePresenter<TransferDetailsView>() {
    private val routeInteractor: RouteInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()
    private val reviewInteractor: ReviewInteractor by inject()

    private val offerMapper: OfferMapper by inject()
    private val profileMapper: ProfileMapper by inject()
    private val routeMapper: RouteMapper by inject()
    private val transferMapper: TransferMapper by inject()

    companion object {
        @JvmField val FIELD_EMAIL = "field_email"
        @JvmField val FIELD_PHONE = "field_phone"
        @JvmField val OPERATION_COPY = "operation_copy"
        @JvmField val OPERATION_OPEN = "operation_open"
    }

    private lateinit var transferModel: TransferModel
    private var routeModel: RouteModel? = null
    private var polyline: PolylineModel? = null
    private var track: CameraUpdate? = null

    internal var transferId = 0L

    @CallSuper
    override fun attachView(view: TransferDetailsView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if (result.error != null) viewState.setError(result.error!!)
            else {
                val transfer = result.model
                transferModel = transferMapper.toView(transfer)
                var offer: Offer? = null

                if (transferModel.status.checkOffers) {
                    val offersResult = utils.asyncAwait { offerInteractor.getOffers(transfer.id) }
                    if(offersResult.error == null && offersResult.model.size == 1){
                        offer = offersResult.model.first()
                        reviewInteractor.offerIdForReview = offer.id
                        if(!transfer.isCompletedTransfer()) viewState.setOffer(offerMapper.toView(offer), transferModel.countChilds)
                    }
                }

                val showRate = offer?.isRated()?.not()?:false
                viewState.setTransfer(transferModel, profileMapper.toView(systemInteractor.account.user.profile), showRate)

                if (transfer.to != null) {
                    val r = utils.asyncAwait { routeInteractor.getRouteInfo(transfer.from.point!!, transfer.to!!.point!!, true, false) }
                    if (r.error == null) setRouteTransfer(transfer, r.model)
                } else if(transfer.duration != null) setHourlyTransfer(transfer)

            }
            viewState.blockInterface(false)

        }
    }

    fun onCenterRouteClick() { track?.let { viewState.centerRoute(it) } }

    fun onCancelRequestClicked() {
        viewState.showAlertCancelRequest()
    }

    private fun setRouteTransfer(transfer: Transfer, route: RouteInfo){
        routeModel =  routeMapper.getView(
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
                    FIELD_EMAIL -> sendEmail(text)
                }
            }
        }
    }

    fun rateTrip(rating: Float){
        if (rating.toInt() == ReviewInteractor.MAX_RATE) {
            reviewInteractor.apply {
                utils.launchSuspend{ sendTopRate() }
                viewState.thanksForRate()
                if (shouldAskRateInMarket) viewState.askRateInPlayMarket()
            }
        }
        else viewState.showDetailRate(rating)
    }

    fun sendReview(mapOfRates: HashMap<String, Int>, feedBackComment: String) {
        utils.launchSuspend {
            val result = utils.asyncAwait { reviewInteractor.sendRates(mapOfRates, feedBackComment) }
            if (result.error != null) {
                /* some error for analytics */
            }
        }
         viewState.thanksForRate()
    }

    fun onRateInStore() {
        systemInteractor.appEntersForMarketRate = ReviewInteractor.APP_RATED_IN_MARKET
        viewState.showRateInPlayMarket()
    }

    fun onReviewCanceled(){
        viewState.closeRateWindow()
        reviewInteractor.rateCanceled()
    }


    fun logEventGetOffer(key: String, value: String) {
        val map = mutableMapOf<String, Any?>()
        map[key] = value
        analytics.logEvent(Analytics.EVENT_GET_OFFER, createStringBundle(key, value), map)
    }
}
