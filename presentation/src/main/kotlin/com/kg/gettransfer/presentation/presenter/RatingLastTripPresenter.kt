package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.ReviewInteractor

import com.kg.gettransfer.domain.model.RouteInfoRequest
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.prefs.PreferencesImpl

import com.kg.gettransfer.presentation.mapper.RouteMapper

import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.view.RatingLastTripView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.core.inject

@InjectViewState
class RatingLastTripPresenter: BasePresenter<RatingLastTripView>() {
    private val orderInteractor: OrderInteractor by inject()
    private val routeMapper: RouteMapper by inject()
    private val transportTypes = systemInteractor.transportTypes.map { it.map() }

    internal var transferId: Long = 0L

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getTransferAndSetupReview()
    }

    private fun getTransferAndSetupReview() {
        utils.launchSuspend {
            fetchResultOnly { transferInteractor.getTransfer(transferId) }.isSuccess()?.let { setupReview(it) }
        }
    }

    private suspend fun setupReview(transfer: Transfer) {
        val routeModel = if (transfer.to != null) createRouteModel(transfer) else null
        viewState.setupReviewForLastTrip(
            transfer.map(transportTypes),
            LatLng(transfer.from.point!!.latitude, transfer.from.point!!.longitude),
            routeModel
        )
    }

    private suspend fun createRouteModel(transfer: Transfer): RouteModel? {
        val route = transfer.from.point?.let { from ->
            transfer.to?.point?.let { to ->
                orderInteractor.getRouteInfo(
                    RouteInfoRequest(
                        from,
                        to,
                        false,
                        false,
                        sessionInteractor.currency.code,
                        null
                    )
                ).model
            }
        }

        return transfer.from.point?.let { fromPoint ->
            transfer.to?.point?.let { toPoint ->
                routeMapper.getView(
                        route?.distance,
                        route?.polyLines,
                        transfer.from.name,
                        transfer.to?.name,
                        fromPoint,
                        toPoint,
                        SystemUtils.formatDateTime(transfer.map(transportTypes).dateTime)
                )
            }
        }
    }

    fun onTransferDetailsClick() {
        router.navigateTo(Screens.Details(transferId))
    }

    fun onReviewCanceled() {
        reviewInteractor.rateCanceled()
        viewState.cancelReview()
    }

    fun onRateClicked(rate: Float) {
        reviewInteractor.setRates(rate)
        if (rate.toInt() == ReviewInteractor.MAX_RATE) {
            logAverageRate(ReviewInteractor.MAX_RATE.toDouble())
            utils.launchSuspend { utils.asyncAwait { reviewInteractor.sendRates() } }
            viewState.cancelReview()
            val showStoreDialog = systemInteractor.appEntersForMarketRate != PreferencesImpl.IMMUTABLE
            if (showStoreDialog) {
                reviewInteractor.shouldAskRateInMarket = true
                logAppReviewRequest()
            }
            viewState.thanksForRate()
        } else {
            viewState.cancelReview()
            viewState.showDetailedReview()
        }
    }

    private fun logAppReviewRequest() = analytics.logSingleEvent(Analytics.EVENT_APP_REVIEW_REQUESTED)

    private fun logAverageRate(rate: Double) =
            analytics.logEvent(Analytics.EVENT_REVIEW_AVERAGE, Analytics.REVIEW, rate)
}