package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.interactor.ReviewInteractor

import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.view.RatingLastTripView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor

import com.kg.gettransfer.sys.domain.Preferences

import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class RatingLastTripPresenter : BaseMapDialogPresenter<RatingLastTripView>() {

    private val worker: WorkerManager by inject { parametersOf("RatingLastTripPresenter") }
    private val getPreferences: GetPreferencesInteractor by inject()

    internal var transferId: Long = 0L

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getTransferAndSetupReview()
        reviewInteractor.reviewSuggested()
    }

    private fun getTransferAndSetupReview() {
        utils.launchSuspend {
            fetchResultOnly { transferInteractor.getTransfer(transferId) }.isSuccess()?.let { setupReview(it) }
        }
    }

    override suspend fun setupReview(transfer: Transfer) {
        super.setupReview(transfer)
        viewState.setupReviewForLastTrip(transfer)
    }

    fun onTransferDetailsClick() {
        router.navigateTo(Screens.Details(transferId))
    }

    fun onRateClicked(rate: Float) {
        reviewInteractor.setRates(rate)
        if (rate.toInt() == ReviewInteractor.MAX_RATE) {
            logAverageRate(ReviewInteractor.MAX_RATE.toDouble())
            worker.main.launch {
                withContext(worker.bg) { reviewInteractor.sendRates() }
                viewState.cancelReview()
                val appEnters = withContext(worker.bg) { getPreferences().getModel() }.appEnters
                val showStoreDialog = appEnters != Preferences.IMMUTABLE
                if (showStoreDialog) {
                    reviewInteractor.shouldAskRateInMarket = true
                    logAppReviewRequest()
                }
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

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
