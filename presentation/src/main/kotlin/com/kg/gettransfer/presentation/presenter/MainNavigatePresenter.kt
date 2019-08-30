package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.interactor.ReviewInteractor

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.Transfer.Companion.filterRateable

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.presentation.view.MainNavigateView

import com.kg.gettransfer.sys.domain.SetAppEntersInteractor
import com.kg.gettransfer.sys.domain.SetNewDriverAppDialogShowedInteractor
import com.kg.gettransfer.sys.presentation.ConfigsManager

import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
@Suppress("TooManyFunctions")
class MainNavigatePresenter : BasePresenter<MainNavigateView>(), CounterEventListener {

    private val configsManager: ConfigsManager by inject()
    private val worker: WorkerManager by inject { parametersOf("MainNavigatePresenter") }
    private val setAppEnters: SetAppEntersInteractor by inject()
    private val setNewDriverAppDialogShowedInteractor: SetNewDriverAppDialogShowedInteractor by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    override fun appInitialized() {
        if (accountManager.isLoggedIn) {
            worker.main.launch {
                if (accountManager.remoteAccount.isDriver && !getPreferences().getModel().isNewDriverAppDialogShowed) {
                    viewState.showNewDriverAppDialog()
                    withContext(worker.bg) { setNewDriverAppDialogShowedInteractor(true) }
                }
            }
            registerPushToken()

            utils.launchSuspend {
                fetchResultOnly { transferInteractor.getAllTransfers() }.isSuccess()?.let { checkReview(it) }
                viewState.setEventCount(accountManager.hasAccount, countEventsInteractor.eventsCount)
            }
        }
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

    override suspend fun onNewOffer(offer: Offer): OfferModel {
        viewState.setEventCount(accountManager.hasAccount, countEventsInteractor.eventsCount)
        return super.onNewOffer(offer)
    }

    private suspend fun checkReview(transfers: List<Transfer>) =
        with(reviewInteractor) {
            if (!isReviewSuggested) {
                checkLastTrip(transfers)
            } else if (shouldAskRateInMarket) {
                utils.launchSuspend {
                    delay(ONE_SEC_DELAY)
                    viewState.askRateInPlayMarket()
                }
            }
        }

    private suspend fun checkLastTrip(transfers: List<Transfer>) {
        getLastTransfer(transfers.filterRateable())?.let { checkToShowReview(it) }
    }

    private fun getLastTransfer(transfers: List<Transfer>) =
        transfers.filter { it.status.checkOffers }.sortedByDescending { it.dateToLocal }.firstOrNull()

    private suspend fun checkToShowReview(transfer: Transfer) =
        fetchResultOnly { offerInteractor.getOffers(transfer.id) }.isSuccess()?.firstOrNull()?.let { offer ->
            if (transfer.offersUpdatedAt != null) fetchDataOnly { transferInteractor.setOffersUpdatedDate(transfer.id) }
            if (offer.isRateAvailable() && offer.isNeededRateOffer()) {
                reviewInteractor.setOfferReview(offer)
                viewState.showRateForLastTrip(transfer.id, offer.vehicle.name, offer.vehicle.color ?: "")
                logTransferReviewRequested()
            }
        }

    fun rateTransfer(transferId: Long, rate: Int) {
        utils.launchSuspend {
            if (!checkTransferForRate(transferId)) return@launchSuspend
            val offersResult = utils.asyncAwait { offerInteractor.getOffers(transferId) }
            if (offersResult.error == null && offersResult.model.size == 1) {
                val offer = offersResult.model.first()
                if (offer.isRateAvailable() && offer.isNeededRateOffer()) {
                    rateOffer(offer, rate)
                }
            }
        }
    }

    private suspend fun checkTransferForRate(transferId: Long): Boolean {
        val transferResult = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
        return if (transferResult.error != null) {
            transferResult.error?.let { err ->
                if (err.isNotFound()) {
                    viewState.setTransferNotFoundError(transferId)
                } else {
                    viewState.setError(err)
                }
            }
            false
        } else {
            val transfer = transferResult.model
            val transferModel = transfer.map(configsManager.configs.transportTypes.map { it.map() })
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

    private fun logTransferReviewRequested() = analytics.logSingleEvent(Analytics.EVENT_TRANSFER_REVIEW_REQUESTED)

    fun redirectToPlayMarket() = worker.main.launch {
        withContext(worker.bg) { setAppEnters(ReviewInteractor.APP_RATED_IN_MARKET) }
        viewState.goToGooglePlay()
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }

    companion object {
        const val ONE_SEC_DELAY = 1000L
    }
}
