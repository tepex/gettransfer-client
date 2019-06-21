package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.ReviewInteractor
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.prefs.PreferencesImpl
import com.kg.gettransfer.presentation.mapper.RouteMapper
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.view.RatingLastTripView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import org.koin.standalone.inject

@InjectViewState
class RatingLastTripPresenter: BasePresenter<RatingLastTripView>() {
    private val reviewInteractor: ReviewInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()
    private val routeMapper: RouteMapper by inject()

    internal var transferId: Long = 0L
    private val offerId: Long
        get() = reviewInteractor.offerIdForReview

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getTransferAndSetupReview()
    }

    private fun getTransferAndSetupReview() {
        utils.launchSuspend {
            fetchResultOnly { transferInteractor.getTransfer(transferId) }
                    .isSuccess()
                    ?.let { setupReview(it) }
        }
    }

    private suspend fun setupReview(transfer: Transfer) {
        val routeModel = if (transfer.to != null) createRouteModel(transfer) else null
        viewState.setupReviewForLastTrip(
                transferMapper.toView(transfer),
                LatLng(transfer.from.point!!.latitude, transfer.from.point!!.longitude),
                routeModel)
    }

    private suspend fun createRouteModel(transfer: Transfer): RouteModel? {
        val route = transfer.from.point?.let { from ->
            transfer.to?.point?.let { to ->
                orderInteractor.getRouteInfo(
                        from, to, false, false,
                        sessionInteractor.currency.code).model
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
                        SystemUtils.formatDateTime(transferMapper.toView(transfer).dateTime)
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
        if (rate.toInt() == ReviewInteractor.MAX_RATE) {
            logAverageRate(ReviewInteractor.MAX_RATE.toDouble())
            reviewInteractor.apply {
                utils.launchSuspend { fetchDataOnly { sendTopRate() } }
                viewState.thanksForRate()
                if (systemInteractor.appEntersForMarketRate != PreferencesImpl.IMMUTABLE) {
                    viewState.askRateInPlayMarket()
                    logAppReviewRequest()
                }
            }
        } else viewState.showDetailedReview(rate, offerId)
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