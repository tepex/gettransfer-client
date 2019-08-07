package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.interactor.ReviewInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Transfer.Companion.filterRateable

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.presentation.view.MainNavigateView

import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.delay

@InjectViewState
class MainNavigatePresenter : BasePresenter<MainNavigateView>(), CounterEventListener {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (accountManager.isLoggedIn) {
            registerPushToken()

            utils.launchSuspend {
                fetchResultOnly { transferInteractor.getAllTransfers() }
                        .isSuccess()?.let { checkReview(it) }
                viewState.setEventCount(accountManager.hasAccount, countEventsInteractor.eventsCount)
            }
        }

        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    override fun attachView(view: MainNavigateView) {
        super.attachView(view)
        countEventsInteractor.addCounterListener(this)
        viewState.setEventCount(accountManager.hasAccount, countEventsInteractor.eventsCount)
        log.debug("MainPresenter.is user logged in: ${accountManager.isLoggedIn}")
    }

    override fun detachView(view: MainNavigateView?) {
        super.detachView(view)
        countEventsInteractor.removeCounterListener(this)
    }

    override fun updateCounter() {
        utils.launchSuspend { viewState.setEventCount(accountManager.hasAccount, countEventsInteractor.eventsCount) }
    }

    override fun onNewOffer(offer: Offer): OfferModel {
        utils.launchSuspend { viewState.setEventCount(accountManager.hasAccount, countEventsInteractor.eventsCount) }
        return super.onNewOffer(offer)
    }

    fun onAboutClick() {
        router.navigateTo(Screens.About(systemInteractor.isOnboardingShowed))
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.ABOUT_CLICKED)
    }

    fun onSettingsClick() {
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.SETTINGS_CLICKED)
    }

    fun onRequestsClick() {
        router.navigateTo(Screens.Requests)
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.TRANSFER_CLICKED)
    }

    fun onLoginClick() {
        login(Screens.PASSENGER_MODE, "")
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.LOGIN_CLICKED)
    }

    fun onSupportClick() {
        router.navigateTo(Screens.Support)
    }

    private suspend fun checkReview(transfers: List<Transfer>) =
        with(reviewInteractor) {
            if (!isReviewSuggested) checkLastTrip(transfers)
            else if (shouldAskRateInMarket)
                utils.launchSuspend {
                    delay(ONE_SEC_DELAY)
                    viewState.askRateInPlayMarket()
                }
        }

    private suspend fun checkLastTrip(transfers: List<Transfer>) {
        getLastTransfer(transfers.filterRateable())
            ?.let { lastTransfer ->
                checkToShowReview(lastTransfer) }
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
                if (offer.isRateAvailable() && offer.isNeededRateOffer()) {
                    reviewInteractor.setOfferReview(offer)
                    viewState.showRateForLastTrip(
                        transfer.id,
                        offer.vehicle.name,
                        offer.vehicle.color ?: ""
                    )
                    logTransferReviewRequested()
                }
            }

    fun rateTransfer(transferId: Long, rate: Int) {
        utils.launchSuspend {
            if (!checkTransferForRate(transferId)) return@launchSuspend
            val offersResult = utils.asyncAwait { offerInteractor.getOffers(transferId) }
            if (offersResult.error == null && offersResult.model.size == 1) {
                offersResult.model.first().let {
                    if (it.isRateAvailable() && it.isNeededRateOffer()) rateOffer(it, rate)
                }
            }
        }
    }

    private suspend fun checkTransferForRate(transferId: Long): Boolean {
        val transferResult = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
        return if (transferResult.error != null) {
            val err = transferResult.error!!
            if (err.isNotFound()) viewState.setTransferNotFoundError(transferId)
            else viewState.setError(err)
            false
        } else {
            val transfer = transferResult.model
            val transferModel = transfer.map(systemInteractor.transportTypes.map { it.map() })
            transferModel.status.checkOffers
        }
    }

    private suspend fun rateOffer(offer: Offer, rate: Int) {
        reviewInteractor.setOfferReview(offer)
        reviewInteractor.setRates(rate.toFloat())
        if (rate == ReviewInteractor.MAX_RATE) {
            utils.asyncAwait { reviewInteractor.sendRates() }
            viewState.thanksForRate()
        } else {
            viewState.showDetailedReview()
        }
    }

    private fun logTransferReviewRequested() =
            analytics.logSingleEvent(Analytics.EVENT_TRANSFER_REVIEW_REQUESTED)

    fun onShareClick() {
        log.debug("Share action")
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.SHARE)
        router.navigateTo(Screens.Share())
    }

    companion object {
        const val ONE_SEC_DELAY = 1000L
    }
}
