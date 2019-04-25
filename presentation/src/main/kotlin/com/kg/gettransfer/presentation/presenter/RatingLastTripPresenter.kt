package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Transfer.Companion.filterRateable
import com.kg.gettransfer.prefs.PreferencesImpl
import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.view.RatingLastTripView
import com.kg.gettransfer.utilities.Analytics
import org.koin.standalone.inject

@InjectViewState
class RatingLastTripPresenter: BasePresenter<RatingLastTripView>() {
    private val reviewInteractor: ReviewInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()
    private val routeMapper: RouteMapper by inject()

    override fun attachView(view: RatingLastTripView) {
        super.attachView(view)
        showRateForLastTrip()
    }

    private fun showRateForLastTrip() {
        utils.launchSuspend {
            fetchResultOnly { transferInteractor.getAllTransfers() }
                    .isSuccess()
                    ?.let {
                        getLastTransfer(it.filterRateable())
                                ?.let { lastTransfer -> checkToShowReview(lastTransfer) }
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
                        if (!offer.isOfferRatedByUser()) {
                            val routeModel = if (transfer.to != null) createRouteModel(transfer) else null
                            reviewInteractor.offerIdForReview = offer.id
                            viewState.setupReviewForLastTrip(
                                    transferMapper.toView(transfer),
                                    LatLng(transfer.from.point!!.latitude, transfer.from.point!!.longitude),
                                    offer.vehicle.name,
                                    offer.vehicle.color ?: "",
                                    routeModel
                            )
                            logTransferReviewRequested()
                        }
                    }

    private suspend fun createRouteModel(transfer: Transfer): RouteModel {
        val route = orderInteractor.getRouteInfo(transfer.from.point!!, transfer.to!!.point!!, false, false, systemInteractor.currency.code).model
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

    private fun logTransferReviewRequested() =
            analytics.logEvent(Analytics.EVENT_TRANSFER_REVIEW_REQUESTED,
                    createEmptyBundle(),
                    emptyMap())

    fun onTransferDetailsClick() {

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
                } else viewState.thanksForRate()
            }
        } else viewState.showDetailedReview(rate)
    }

    private fun logAppReviewRequest() =
            analytics.logEvent(
                    Analytics.EVENT_APP_REVIEW_REQUESTED,
                    createEmptyBundle(),
                    emptyMap()
            )

    private fun logAverageRate(rate: Double) =
            analytics.logEvent(
                    Analytics.REVIEW_AVERAGE,
                    createStringBundle(Analytics.REVIEW, rate.toString()),
                    mapOf(Analytics.REVIEW to rate)
            )
}